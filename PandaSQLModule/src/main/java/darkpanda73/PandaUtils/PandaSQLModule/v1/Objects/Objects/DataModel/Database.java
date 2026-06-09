package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.SQLObject;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.SQLParser;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Enums.SQLType;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.Data;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Joins.Join;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.SubDatabases.MySQLDatabase;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.SubDatabases.SQLiteDatabase;
import lombok.Setter;

@lombok.Data
@Setter(lombok.AccessLevel.NONE)
public abstract class Database {

	protected SQLObject sql;
	private List<Table> tables = new ArrayList<>();
	private String prefix;
	private int version;

	public Database(ConnectionData data, int version) throws ClassNotFoundException {
		this(data, version, "");
	};

	public Database(ConnectionData data, int version, String prefix) throws ClassNotFoundException {
		this.sql = new SQLObject(data);
		this.version = version;
		this.prefix = prefix;
	}

	public ConnectionData getConnectionData() {
		return this.sql.getConnectionData();
	}

	public boolean isConnected() {
		return this.sql.isConnected();
	}

	public boolean connect() {
		return connect(new SQLParser[0]);
	}

	public boolean connect(SQLParser... parsers) {
		if (!this.sql.connect())
			return false;
		if (!updateTables(String.valueOf(this.version), parsers))
			return false;
		return true;
	}

	public void close() {
		this.sql.close();
	}

	public static Database createDatabase(ConnectionData data, int version) throws ClassNotFoundException {
		return createDatabase(data, version, "");
	}

	public static Database createDatabase(ConnectionData data, int version, String prefix)
			throws ClassNotFoundException {
		return data.getType() == SQLType.MYSQL ? new MySQLDatabase(data, version, prefix)
				: (data.getType() == SQLType.SQLITE ? new SQLiteDatabase(data, version, prefix) : null);
	}

	public Database addTable(Table table) {
		tables.add(table);
		return this;
	}

	public Table getTable(String name) {
		for (Table table : tables)
			if (table.getName().equals(name) || table.getName().equals(prefix + name))
				return table;
		return null;
	}

	public List<String> selectTables() {
		return new ArrayList<>();
	}

	public boolean updateTables(String version, SQLParser... parsers) {
		return false;
	}

	public boolean createTable(Table table) {
		return false;
	}

	public boolean truncateTable(Table table) {
		return false;
	}

	public boolean dropTable(Table table) {
		return false;
	}

	public boolean setForeignKeyCheck(boolean enabled) {
		return false;
	}

	public ResultSet select(List<Table> allcolumns, Condition... conditions) {
		return select(allcolumns, new ArrayList<>(), conditions);
	}

	public ResultSet select(List<Table> allcolumns, List<Column> columns, Condition... conditions) {
		return select(allcolumns, columns, new ArrayList<>(), conditions);
	}

	public ResultSet select(List<Table> allcolumns, List<Column> columns, List<Join> joins, Condition... conditions) {
		return selectDistinct(false, allcolumns, columns, joins, conditions);
	}

	public ResultSet selectDistinct(List<Table> allcolumns, Condition... conditions) {
		return selectDistinct(allcolumns, new ArrayList<>(), conditions);
	}

	public ResultSet selectDistinct(List<Table> allcolumns, List<Column> columns, Condition... conditions) {
		return selectDistinct(true, allcolumns, columns, new ArrayList<>(), conditions);
	}

	public ResultSet selectDistinct(boolean distinct, List<Table> allcolumns, List<Column> columns, List<Join> joins,
			Condition... conditions) {
		return null;
	}

	public boolean insert(Table table, Map<Column, Data> values) {
		return insertMultiple(table, Arrays.asList(values));
	}

	public boolean insertMultiple(Table table, List<Map<Column, Data>> entries) {
		return false;
	}

	public boolean insertMultipleIgnore(Table table, List<Map<Column, Data>> entries) {
		return false;
	}

	public int insertWithID(Table table, Map<Column, Data> values) {
		return -1;
	}

	public boolean update(Table table, Map<Column, Data> values, Condition... conditions) {
		return false;
	}

	public boolean insertOrUpdate(Table table, Map<Column, Data> values, Condition... conditions) {
		return false;
	}

	public boolean delete(Table table, Condition... conditions) {
		return false;
	}
}
