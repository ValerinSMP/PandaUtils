package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.List;

import org.bukkit.Bukkit;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.Data;

public class SQLObject {
	private SQLConnection con;
	private ConnectionData data;

	public SQLObject(ConnectionData data) throws ClassNotFoundException {
		this.data = data;
		con = new SQLConnection(this.data);
	}

	public ConnectionData getConnectionData() {
		return data;
	}

	public boolean connect() {
		return con.connect();
	}

	public boolean isAutoCommit() {
		return con.isAutoCommit();
	}

	public boolean execute(String statement, List<Data> args) {
		return execute(statement, args.toArray(new Data[args.size()]));
	}

	public boolean execute(String statement, Data... args) {
		boolean result = false;
		if (!isConnected()) {
			con.connect();
			if (!isConnected()) {
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> ERROR!!! Trying to reconnect to database without succeed");
				return false;
			}
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.getConnection().prepareStatement(statement);
			if (args != null)
				data(stmt, args);
			long t = System.currentTimeMillis();
			stmt.execute();
			long f = System.currentTimeMillis() - t;
			if (f > 200L)
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> WARNING!!! Task has been completed out of the maximum established time.");
			result = true;
		} catch (SQLException e) {
			if (!isAutoCommit()) {
				rollback();
			}
		} finally {
			if (stmt != null)
				close(stmt);
		}
		return result;
	}

	public int executeWithId(String statement, List<Data> args) {
		return executeWithId(statement, args.toArray(new Data[args.size()]));
	}

	public int executeWithId(String statement, Data... args) {
		if (!isConnected()) {
			con.connect();
			if (!isConnected()) {
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> ERROR!!! Trying to reconnect to database without succeed");
				return -1;
			}
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.getConnection().prepareStatement(statement, Statement.RETURN_GENERATED_KEYS);
			if (args != null)
				data(stmt, args);
			long t = System.currentTimeMillis();
			stmt.execute();
			long f = System.currentTimeMillis() - t;
			if (f > 200L)
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> WARNING!!! Task has been completed out of the maximum established time.");
			ResultSet rs = stmt.getGeneratedKeys();
			if (rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			if (!isAutoCommit()) {
				rollback();
			}
		} finally {
			if (stmt != null)
				close(stmt);
		}
		return -1;
	}

	public ResultSet query(String statement, List<Data> args) {
		return query(statement, args.toArray(new Data[args.size()]));
	}

	public ResultSet query(String statement, Data... args) {
		ResultSet r = null;
		if (!isConnected()) {
			con.connect();
			if (!isConnected()) {
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> ERROR!!! Trying to reconnect to database without succeed");
				return null;
			}
		}
		PreparedStatement stmt = null;
		try {
			stmt = con.getConnection().prepareStatement(statement);
			if (args != null)
				data(stmt, args);
			long t = System.currentTimeMillis();
			r = stmt.executeQuery();
			long f = System.currentTimeMillis() - t;
			if (f > 250L)
				Bukkit.getConsoleSender()
						.sendMessage("<SQLib> WARNING!!! Task has been completed out of the maximum established time.");
		} catch (SQLException e) {
			if (!isAutoCommit()) {
				rollback();
			}
		}
		return r;
	}

	protected void data(PreparedStatement stmt, Data... args) throws SQLException {
		for (int i = 0, j = 0; i + j < args.length;) {
			Data d = args[i + j];
			if (d.getType() == Types.OTHER) {
				++j;
				continue;
			}
			if (d.getValue() == null) {
				stmt.setNull(++i, d.getType());
				continue;
			} else {
				stmt.setObject(++i, d.getValue(), d.getType());
				continue;
			}
		}
	}

	public boolean isConnected() {
		if (con != null) {
			try (PreparedStatement stmt = con.getConnection().prepareStatement("SELECT 1;")) {
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
			con.getConnection().setAutoCommit(enabled);
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public boolean commit() {
		try {
			con.getConnection().commit();
			setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			setAutoCommit(true);
			return false;
		}
	}

	public boolean rollback() {
		try {
			con.getConnection().rollback();
			setAutoCommit(true);
			return true;
		} catch (SQLException e) {
			setAutoCommit(true);
			return false;
		}
	}

	public void close(AutoCloseable obj) {
		try {
			if (obj != null)
				obj.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (con != null)
			close(con.getConnection());
	}
}
