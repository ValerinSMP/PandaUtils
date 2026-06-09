package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.AlterTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.DeleteStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.DropTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.InsertStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualQueryStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.SelectStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.Parameter;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.UpdateStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils.SQLServiceUtilities;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;
import lombok.AllArgsConstructor;
import lombok.Getter;

public abstract class SQLConnection implements AutoCloseable {

	private static final int MAX_RETRIES = 5;
	private static final int DELAY_IN_MILLIS = 5000;

	private AsyncQueue<Runnable> queries;

	public SQLConnection() {
		this.queries = new AsyncQueue<>();
		this.queries.subscribe((obs) -> obs.run());
	}

	public boolean isAutoCommit() {
		try {
			return this.getConnection().getAutoCommit();
		} catch (SQLException e) {
			return false;
		}
	}

	public ResultSet executeQuery(SelectStatement selectStatement) throws Throwable {
		return this.privateExecuteQuery(selectStatement.build(this));
	}

	public void executeCreateTable(CreateTableStatement createTableStatement) throws Throwable {
		this.privateExecute(createTableStatement.build(this));
	}

	public void executeInsert(InsertStatement insertStatement) throws Throwable {
		if (insertStatement.getEntries().size() > 0) {
			this.privateExecute(insertStatement.build(this));
		}
	}

	public void executeDelete(DeleteStatement deleteStatement) throws Throwable {
		this.privateExecute(deleteStatement.build(this));
	}

	public void executeDropTable(DropTableStatement dropTableStatement) throws Throwable {
		this.privateExecute(dropTableStatement.build(this));
	}

	public void executeUpdate(UpdateStatement updateStatement) throws Throwable {
		this.privateExecute(updateStatement.build(this));
	}

	public void executeAlterTable(AlterTableStatement alterTableStatement) throws Throwable {
		this.privateExecute(alterTableStatement.build(this));
	}

	public ResultSet executeManualQuery(ManualQueryStatement manualQueryStatement) throws Throwable {
		return this.privateExecuteQuery(manualQueryStatement.build());
	}

	public void executeManual(ManualStatement manualStatement) throws Throwable {
		this.privateExecute(manualStatement.build());
	}

	protected ResultSet privateExecuteQuery(
			darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.PreparedStatement preparedStatement)
			throws Throwable {
		QueryResult<ResultSet> queryResult = privateExecuteGeneric(() -> {
			try {
				if (SQLServiceUtilities.isPrintQueriesInConsoleEnabled()) {
					PandaPluginClass.getSimpleLogger().sendDebug(getClass(), preparedStatement.getStatement());
				}

				PreparedStatement stmt = getConnection().prepareStatement(preparedStatement.getStatement());
				data(stmt, preparedStatement.getParameters());
				return new QueryResult<ResultSet>(stmt.executeQuery(), null);
			} catch (Throwable e) {
				if (!isAutoCommit()) {
					rollback();
				}
				return new QueryResult<ResultSet>(null, e);
			}
		});

		if (queryResult.getException() != null) {
			throw queryResult.getException();
		}

		return queryResult.getValue();
	}

	protected void privateExecute(
			darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.PreparedStatement preparedStatement)
			throws Throwable {
		QueryResult<Void> queryResult = privateExecuteGeneric(() -> {
			try {
				if (SQLServiceUtilities.isPrintQueriesInConsoleEnabled()) {
					PandaPluginClass.getSimpleLogger().sendDebug(getClass(), preparedStatement.getStatement());
				}

				PreparedStatement stmt = getConnection().prepareStatement(preparedStatement.getStatement());
				data(stmt, preparedStatement.getParameters());
				stmt.execute();
				stmt.close();
			} catch (Throwable e) {
				if (!isAutoCommit()) {
					rollback();
				}
				return new QueryResult<Void>(null, e);
			}
			return new QueryResult<Void>(null, null);

		});

		if (queryResult.getException() != null) {
			throw queryResult.getException();
		}
	}

	public synchronized <T> QueryResult<T> privateExecuteGeneric(Callable<QueryResult<T>> callable)
			throws InterruptedException, ExecutionException, TimeoutException, SQLException {
		int retry = 0;

		while (retry++ < MAX_RETRIES) {
			try {
				if (!isConnected()) {
					connect();
					if (!isConnected()) {
						PandaPluginClass.getSimpleLogger().sendError(
								"Tried to reconnect to the database without success. It is recommended to check the database or restart the server. Some data my not be saved and some data may be lost.");
						throw new SQLException("Unable to reconnect to the database");
					}
				}

				FutureTask<QueryResult<T>> result = new FutureTask<>(callable);

				queries.add(() -> result.run());

				return result.get(30000, TimeUnit.MILLISECONDS);
			} catch (Throwable e) {
				if (e instanceof TimeoutException && retry != MAX_RETRIES) {
					PandaPluginClass.getSimpleLogger().sendWarning(String.format(
							"Unable to send request to SQL due timeout exception, retrying in 5 seconds. (%d/%d)",
							retry, MAX_RETRIES));
					Thread.sleep(DELAY_IN_MILLIS);
				} else {
					PandaPluginClass.getSimpleLogger()
							.sendWarning(String.format("Unable to send request to SQL, query aborted."));
					throw e;
				}
			}
		}

		throw new SQLException("Unable to process SQL query.");
	}

	public boolean isConnected() {
		if (getConnection() != null) {
			try (PreparedStatement stmt = getConnection().prepareStatement("SELECT 1;")) {
				try (ResultSet set = stmt.executeQuery()) {
					if (set.next()) {
						return set.getInt("1") == 1;
					}
				}
			} catch (SQLException e) {
			}
		}
		return false;
	}

	public boolean setAutoCommit(boolean enabled) {
		try {
			getConnection().setAutoCommit(enabled);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public void commit() throws SQLException {
		getConnection().commit();
	}

	public void rollback() throws SQLException {
		getConnection().rollback();
	}

	@Override
	public void close() throws Exception {
		if (getConnection() != null) {
			try {
				this.queries.join();
			} catch (Exception e) {
				PandaPluginClass.getSimpleLogger().sendError("Forced SQL queries to stop, some data may be lost!");
				e.printStackTrace();
			}
			getConnection().close();
		}
	}

	private void data(PreparedStatement stmt, List<Parameter> parameters) throws SQLException {
		for (int i = 0; i < parameters.size();) {
			Parameter parameter = parameters.get(i);

			if (parameter.getData() == null) {
				stmt.setNull(++i, Types.NULL);
			} else {
				stmt.setObject(++i, parameter.getData(), parameter.getType());
			}
		}
	}

	public String getPrefix() {
		return "";
	}

	public boolean isMySQLConnection() {
		return false;
	}

	public boolean isSQLiteConnection() {
		return false;
	}

	public abstract boolean connect();

	public abstract Connection getConnection();

	public abstract void setForeignKeyCheck(boolean check) throws Throwable;

	@AllArgsConstructor
	@Getter
	public static class QueryResult<T> {

		private T value;

		private Throwable exception;

	}
}
