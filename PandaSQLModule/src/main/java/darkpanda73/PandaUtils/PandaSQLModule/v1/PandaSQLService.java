package darkpanda73.PandaUtils.PandaSQLModule.v1;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Annotations.PandaSQLConfig;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.SQLParser;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Enums.SQLType;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.ConnectionData.MySQLConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.ConnectionData.SQLiteConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Database;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;

@PandaService(loadOn = LoadStep.LOAD, priority = 1)
public abstract class PandaSQLService {

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaPluginClass plugin;

	private Database database;

	private PandaSQLConfig pandaSQLConfig;
	private String sqlitePath;

	private PandaBooleanField mysqlEnabled;
	private PandaStringField mysqlProtocol;
	private PandaStringField mysqlHost;
	private PandaIntegerField mysqlPort;
	private PandaStringField mysqlDatabase;
	private PandaStringField mysqlUsername;
	private PandaStringField mysqlPassword;
	private PandaStringField mysqlParameters;
	private PandaStringField mysqlTablePrefix;

	@PostInjectMethod
	private void postInject() {
		pandaSQLConfig = this.getClass().getAnnotation(PandaSQLConfig.class);

		if (pandaSQLConfig != null) {
			if (pandaSQLConfig.allowMySQL() && pandaSQLConfig.allowSQLite()) {
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

			PandaYamlFile config = filesService.getYaml("config");
			if (config != null && pandaSQLConfig.allowMySQL()) {
				if (pandaSQLConfig != null && pandaSQLConfig.allowSQLite()) {
					config.registerFields(mysqlEnabled);
				}

				config.registerFields(mysqlProtocol, mysqlHost, mysqlPort, mysqlDatabase, mysqlUsername, mysqlPassword,
						mysqlParameters, mysqlTablePrefix);
			}
		}
	}

	@LoadMethod
	public void load() throws ReportError {
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

		this.sqlitePath = folder.getFile().getPath() + "/DB.sqlite";

		if (!connect()) {
			throw new ReportResult.ReportError("Unable to connect the database", this.pandaSQLConfig.required(), null);
		}
	}

	@UnloadMethod
	public void unload() {
		if (this.database != null && this.database.isConnected()) {
			this.database.close();
		}
	}

	public boolean connect(ConnectionData data, int version, String prefix, SQLParser... parsers) throws Exception {

		this.database = Database.createDatabase(data, version, prefix);

		if (this.database == null)
			throw new Exception("SQLType must be a valid SQL engine, can't be null!");

		generateTables();

		plugin.sendInfo(getClass(), String.format("Connecting to database [Using: %s]...",
				this.database.getConnectionData().getType().name()));

		if (!this.database.connect(parsers)) {
			return false;
		}

		if (this.database.getConnectionData().getType() == SQLType.MYSQL) {
			MySQLConnectionData mysqldata = (MySQLConnectionData) this.database.getConnectionData();
			plugin.sendInfo(getClass(), "Connected successfully to " + mysqldata.getHost() + ":" + mysqldata.getPort()
					+ " for database " + mysqldata.getDatabase() + " with username " + mysqldata.getUsername() + ".");
		} else if (data.getType() == SQLType.SQLITE) {
			SQLiteConnectionData sqlitedata = (SQLiteConnectionData) this.database.getConnectionData();
			plugin.sendInfo(getClass(), "Connected successfully to " + sqlitedata.toString() + " file.");
		}
		return true;
	}

	public boolean connect() {
		try {
			ConnectionData connectionData = null;

			if (pandaSQLConfig.allowMySQL() && (!pandaSQLConfig.allowSQLite() || mysqlEnabled.getContent())) {
				connectionData = new MySQLConnectionData(mysqlProtocol.getContent(), mysqlHost.getContent(),
						mysqlPort.getContent(), mysqlDatabase.getContent(), mysqlUsername.getContent(),
						mysqlPassword.getContent(), mysqlParameters.getContent().split("&"));
			}

			if (connectionData == null && pandaSQLConfig.allowSQLite()) {
				connectionData = new SQLiteConnectionData(sqlitePath);
			}

			return this.connect(connectionData, pandaSQLConfig.version(), mysqlTablePrefix.getContent(),
					getSQLParsers());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isConnected() {
		return getDatabase() != null && getDatabase().isConnected();
	}

	public Database getDatabase() {
		return this.database;
	}

	protected SQLParser[] getSQLParsers() {
		return new SQLParser[0];
	}

	protected abstract void generateTables();

}
