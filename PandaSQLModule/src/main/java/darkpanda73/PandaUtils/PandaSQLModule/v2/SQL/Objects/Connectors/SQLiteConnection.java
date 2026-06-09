package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.InsertStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualStatement;
import lombok.Getter;

public class SQLiteConnection extends SQLConnection {

	private File file;

	private String connectionPath;

	private @Getter Connection connection;

	public SQLiteConnection(File file) throws ClassNotFoundException, IOException {
		super();

		Class.forName("org.sqlite.JDBC");

		this.file = file;

		this.connectionPath = "jdbc:sqlite:" + this.file.getPath();

		if (file.getParentFile() != null && !file.getParentFile().exists()) {
			file.getParentFile().mkdirs();
		}

		if (!file.exists()) {
			file.createNewFile();
		}
	}

	@Override
	public boolean connect() {
		try {
			this.connection = DriverManager.getConnection(connectionPath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void setForeignKeyCheck(boolean check) throws Throwable {
		this.executeManual(ManualStatement.inst("PRAGMA foreign_keys=" + (check ? "on" : "off")));
	}

	@Override
	public void executeInsert(InsertStatement insertStatement) throws Throwable {
		try {
			super.executeInsert(insertStatement);
		} catch (SQLException e) {
			if (insertStatement.getConditionIfExists() != null) {
				try {
					setAutoCommit(false);
					insertStatement.buildUpdate(this).forEach(preparedStatement -> {
						try {
							privateExecute(preparedStatement);
						} catch (Throwable e2) {
							throw new RuntimeException(e2);
						}
					});
				} catch (RuntimeException e2) {
					if (e2.getCause() != null) {
						throw e2.getCause();
					}
					throw e2;
				} finally {
					setAutoCommit(true);
				}
			} else {
				throw e;
			}
		}
	}

	@Override
	public boolean isSQLiteConnection() {
		return true;
	}

}
