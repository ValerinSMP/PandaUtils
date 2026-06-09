package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.ManualStatement;
import lombok.Getter;

public class MySQLConnection extends SQLConnection {

	private String protocol;
	private String host;
	private int port;
	private String database;
	private String user;
	private String password;
	private String prefix;
	private String[] parameters;

	private String connectionPath;

	private @Getter Connection connection;

	public MySQLConnection(String protocol, String host, int port, String database, String user, String password,
			String prefix, String[] parameters) throws ClassNotFoundException {
		super();

		Class.forName("com.mysql.jdbc.Driver");

		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		this.prefix = prefix;
		this.parameters = parameters;

		this.connectionPath = this.protocol + "//" + this.host + ":" + this.port + "/" + this.database;
		if (this.parameters != null && this.parameters.length > 0) {
			this.connectionPath += "?" + Arrays.stream(this.parameters).collect(Collectors.joining("&"));
		}
	}

	@Override
	public String getPrefix() {
		return prefix != null ? prefix : "";
	}

	@Override
	public boolean connect() {
		try {
			this.connection = this.user != null && this.password != null
					? DriverManager.getConnection(connectionPath, user, password)
					: DriverManager.getConnection(connectionPath);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void setForeignKeyCheck(boolean check) throws Throwable {
		this.executeManual(ManualStatement.inst("SET FOREIGN_KEY_CHECKS=" + (check ? "1" : "0")));
	}

	@Override
	public boolean isMySQLConnection() {
		return true;
	}

}
