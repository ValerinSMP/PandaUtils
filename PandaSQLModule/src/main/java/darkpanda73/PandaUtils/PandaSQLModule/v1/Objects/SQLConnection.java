package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects;

import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.ConnectionData.MySQLConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.ConnectionData.SQLiteConnectionData;

public class SQLConnection {
	private String path, user, password;
	private java.sql.Connection con;

	public SQLConnection(ConnectionData data) throws ClassNotFoundException {
		switch (data.getType()) {
		case MYSQL:
			Class.forName("com.mysql.jdbc.Driver");
			MySQLConnectionData mysqldata = (MySQLConnectionData) data;
			this.path = mysqldata.getProtocol() + "//" + mysqldata.getHost() + ":" + mysqldata.getPort() + "/"
					+ mysqldata.getDatabase();
			this.user = mysqldata.getUsername();
			this.password = mysqldata.getPassword();
			if (mysqldata.getParameters().length != 0) {
				this.path += "?" + mysqldata.getParameters()[0];
				for (int i = 1; i < mysqldata.getParameters().length; i++)
					this.path += "&" + mysqldata.getParameters()[i];
			}
			break;
		case SQLITE:
			Class.forName("org.sqlite.JDBC");
			SQLiteConnectionData sqlitedata = (SQLiteConnectionData) data;
			this.path = "jdbc:sqlite:" + sqlitedata.getPath();
			File f = new File(sqlitedata.getPath());
			File folder = f.getParentFile();
			if (!folder.exists())
				folder.mkdirs();
			if (!f.exists()) {
				try {
					f.createNewFile();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		default:
			break;
		}
	}

	public boolean isAutoCommit() {
		try {
			return this.con.getAutoCommit();
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean connect() {
		try {
			con = DriverManager.getConnection(path, user, password);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public java.sql.Connection getConnection() {
		return this.con;
	}
}
