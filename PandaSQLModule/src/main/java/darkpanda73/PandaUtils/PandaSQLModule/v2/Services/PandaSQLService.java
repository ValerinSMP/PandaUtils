package darkpanda73.PandaUtils.PandaSQLModule.v2.Services;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaSQLModule.v2.Annotations.PandaSQLConfigV2;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs.SQLChangelog;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.MySQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLiteConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.CreateTableStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.InsertStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.SelectStatement;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.EqualsCondition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils.SQLServiceUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;
import lombok.Getter;

@PandaService(loadOn = LoadStep.LOAD, priority = 1)
public abstract class PandaSQLService {

	public static final Table CHANGELOGS_TABLE = new Table("Changelogs").addColumns(
			new Column("Identifier", Types.INTEGER).setPrimary(true),
			new Column("Description", Types.VARCHAR, "VARCHAR(255)"));

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaPluginClass plugin;

	private PandaSQLConfigV2 pandaSQLConfig;
	private File sqliteFile;

	private @Getter SQLConnection sqlConnection;

	private @Getter PandaBooleanField mysqlEnabled;
	private @Getter PandaStringField mysqlProtocol;
	private @Getter PandaStringField mysqlHost;
	private @Getter PandaIntegerField mysqlPort;
	private @Getter PandaStringField mysqlDatabase;
	private @Getter PandaStringField mysqlUsername;
	private @Getter PandaStringField mysqlPassword;
	private @Getter PandaStringField mysqlParameters;
	private @Getter PandaStringField mysqlTablePrefix;
	private @Getter PandaBooleanField sqlPrintQueriesInConsole;

	private @Getter AsyncQueue<Runnable> asyncQueue;

	@PostInjectMethod
	private void postInject() {
		pandaSQLConfig = this.getClass().getAnnotation(PandaSQLConfigV2.class);

		if (pandaSQLConfig != null) {
			if (pandaSQLConfig.allowMySQL() && !pandaSQLConfig.forceMySQL()) {
				mysqlEnabled = new PandaBooleanField("MySQL.Enable", false);
			}

			mysqlProtocol = new PandaStringField("MySQL.Protocol", "jdbc:mysql:");
			mysqlHost = new PandaStringField("MySQL.Hostname", "localhost");
			mysqlPort = new PandaIntegerField("MySQL.Port", 3306);
			mysqlDatabase = new PandaStringField("MySQL.Database", "database");
			mysqlUsername = new PandaStringField("MySQL.Username", "username");
			mysqlPassword = new PandaStringField("MySQL.Password", "password");
			mysqlParameters = new PandaStringField("MySQL.Parameters",
					"sslMode=DISABLED&connectionTimeout=3000&socketTimeout=3000");
			mysqlTablePrefix = new PandaStringField("MySQL.Table-prefix", "");
			sqlPrintQueriesInConsole = new PandaBooleanField("SQL.Print-queries-in-console", false);

			PandaYamlFile config = filesService.getYaml("config");
			if (config != null && pandaSQLConfig.allowMySQL()) {
				if (pandaSQLConfig != null && !pandaSQLConfig.forceMySQL()) {
					config.registerFields(mysqlEnabled);
				}

				config.registerFields(mysqlProtocol, mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword,
						mysqlParameters, mysqlTablePrefix, sqlPrintQueriesInConsole);
			}
		}
	}

	@LoadMethod
	public void load() throws ReportError {
		this.asyncQueue = new AsyncQueue<Runnable>();
		this.asyncQueue.subscribe((runnable) -> runnable.run());

		InjectUtils.inject(SQLServiceUtilities.class, this);

		if (pandaSQLConfig == null) {
			throw new ReportResult.ReportError(
					"Unable to determine the SQL configuration. Please use the 'PandaSQLConfig' on your PandaSQLService to be able to determine the required information to implement databases.");
		}

		if (filesService == null) {
			throw new ReportResult.ReportError(
					"Unable to get an instance of PandaFilesService. It's required in order to determine the position where the SQLite should be placed. Please include the PandaFilesService on your project.");
		}

		PandaFolder folder = filesService.getFolder("plugin");

		if (folder == null) {
			throw new ReportResult.ReportError(
					"Unable to retrieve the 'plugin' folder. This folder must be registered on the PandaFilesService. Please register a new instance of PandaFile with the ID 'plugin'.");
		}

		this.sqliteFile = new File(folder.getFile().getPath(), "DB.sqlite");

		try {
			connect();
		} catch (ReportResult.ReportError e) {
			throw e;
		} catch (Exception e) {
			throw new ReportResult.ReportError("Unable to connect the database", this.pandaSQLConfig.required(), e);
		}

		try {
			update();
		} catch (Throwable e) {
			throw new ReportResult.ReportError("Unable to update tables", this.pandaSQLConfig.required(), e);
		}
	}

	@ReloadMethod
	public void reload() throws InterruptedException {
		this.asyncQueue.join();
	}

	@UnloadMethod
	public void unload() throws Exception {
		this.asyncQueue.join();
		this.asyncQueue.stop();

		if (this.sqlConnection != null && this.sqlConnection.isConnected()) {
			this.sqlConnection.close();
		}
	}

	public void connect() throws ReportResult.ReportError, Exception {
		if (pandaSQLConfig.allowMySQL() && (pandaSQLConfig.forceMySQL() || mysqlEnabled.isTrue())) {
			this.sqlConnection = generateMySQLConnection();
			this.sqlConnection.connect();
			PandaPluginClass.getSimpleLogger()
					.sendInfo(String.format("Connected successfully to %s:%d for database %s with username %s",
							mysqlHost.getContent(), mysqlPort.getContent(), mysqlDatabase.getContent(),
							mysqlUsername.getContent()));
			return;
		}

		if (this.sqlConnection == null && pandaSQLConfig.allowSQLite()) {
			this.sqlConnection = generateSQLiteConnection();
			this.sqlConnection.connect();
			PandaPluginClass.getSimpleLogger().sendInfo(String.format("Connected successfully to %s file", sqliteFile));
			return;
		}

		throw new ReportResult.ReportError("No valid SQL configuration has been found", this.pandaSQLConfig.required());
	}

	public MySQLConnection generateMySQLConnection() throws Exception {
		if (mysqlProtocol.getContent() == null) {
			throw new NullPointerException("Missing MySQL protocol");
		}

		if (mysqlHost.getContent() == null) {
			throw new NullPointerException("Missing MySQL host");
		}

		if (mysqlPort.getContent() == null) {
			throw new NullPointerException("Missing MySQL port");
		}

		if (mysqlDatabase.getContent() == null) {
			throw new NullPointerException("Missing MySQL database");
		}

		if (mysqlUsername.getContent() == null) {
			throw new NullPointerException("Missing MySQL username");
		}

		if (mysqlPassword.getContent() == null) {
			throw new NullPointerException("Missing MySQL password");
		}

		return new MySQLConnection(mysqlProtocol.getContent(), mysqlHost.getContent(), mysqlPort.getContent(),
				mysqlDatabase.getContent(), mysqlUsername.getContent(), mysqlPassword.getContent(),
				mysqlTablePrefix.getContent(),
				mysqlParameters.getContent() != null ? mysqlParameters.getContent().split("&") : new String[0]);
	}

	public SQLiteConnection generateSQLiteConnection() throws Exception {
		return new SQLiteConnection(sqliteFile);
	}

	public boolean isConnected() {
		return getSqlConnection() != null && getSqlConnection().isConnected();
	}

	public Collection<SQLChangelog> getSQLChangelogs() {
		return Collections.emptyList();
	}

	public void update() throws Throwable {
		this.update(this.getSqlConnection());
	}

	public void update(SQLConnection connection) throws Throwable {
		connection.executeCreateTable(CreateTableStatement.inst(CHANGELOGS_TABLE));

		Collection<SQLChangelog> changelogs = getSQLChangelogs();

		for (SQLChangelog changelog : changelogs) {
			try (ResultSet set = connection.executeQuery(SelectStatement.inst().addTable(CHANGELOGS_TABLE).setCondition(
					EqualsCondition.inst(CHANGELOGS_TABLE.getColumn("Identifier"), changelog.getIdentifier())))) {
				if (!set.next()) {
					try {
						PandaPluginClass.getSimpleLogger()
								.sendDebug(String.format("Changelog '%d - %s' is not applied. Executing changes.",
										changelog.getIdentifier(), changelog.getDescription()));
						connection.setAutoCommit(false);
						changelog.execute(connection);
						connection.executeInsert(InsertStatement.inst(CHANGELOGS_TABLE)
								.addEntry(changelog.getIdentifier(), changelog.getDescription()));
						connection.commit();
						PandaPluginClass.getSimpleLogger()
								.sendDebug(String.format("Changelog '%d - %s' applied successfully.",
										changelog.getIdentifier(), changelog.getDescription()));
					} catch (Throwable e) {
						PandaPluginClass.getSimpleLogger()
								.sendError(String.format(
										"Changelog '%d - %s' could not be applied. Please check the following error.",
										changelog.getIdentifier(), changelog.getDescription()));
						e.printStackTrace();
						connection.rollback();
						throw e;
					} finally {
						connection.setAutoCommit(true);
					}
				}
			}
		}
	}

	public void transferData(SQLConnection destination, List<Table> tables) throws Throwable {
		try {
			this.update(destination);

			tables.forEach(table -> {
				try {
					destination.executeCreateTable(CreateTableStatement.inst(table));
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});

			destination.setForeignKeyCheck(false);
			destination.setAutoCommit(false);

			tables.forEach(table -> {
				try (ResultSet set = this.getSqlConnection().executeQuery(SelectStatement.inst().addTable(table))) {
					int entries = 0;
					InsertStatement insert = InsertStatement.inst(table);

					while (set.next()) {
						List<Object> values = table.getColumns().stream().map(column -> {
							try {
								return set.getObject(column.getName());
							} catch (SQLException e) {
								throw new RuntimeException(e);
							}
						}).collect(Collectors.toList());

						insert.addEntry(values.toArray(new Object[values.size()]));

						if (++entries >= 500) {
							entries = 0;

							destination.executeInsert(insert);
							insert = InsertStatement.inst(table);
						}
					}

					if (entries > 0) {
						destination.executeInsert(insert);
					}
				} catch (RuntimeException e) {
					throw e;
				} catch (Throwable e) {
					throw new RuntimeException(e);
				}
			});
		} catch (RuntimeException e) {
			if (e.getCause() != null) {
				throw e.getCause();
			}
		} finally {
			destination.setForeignKeyCheck(true);
			destination.setAutoCommit(true);
		}
	}

}
