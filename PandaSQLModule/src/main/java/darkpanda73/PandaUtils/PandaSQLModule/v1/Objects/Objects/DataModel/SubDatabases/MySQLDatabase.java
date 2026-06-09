package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.SubDatabases;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.SQLParser;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.SQLParser.When;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.Data;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Database;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Table;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Joins.Join;

public class MySQLDatabase extends Database {

	public MySQLDatabase(ConnectionData data, int version) throws ClassNotFoundException {
		super(data, version);
	}

	public MySQLDatabase(ConnectionData data, int version, String prefix) throws ClassNotFoundException {
		super(data, version, prefix);
	}

	@Override
	public List<String> selectTables() {
		List<String> tables = new ArrayList<>();
		try {
			ResultSet set = this.sql.query("SHOW TABLES;");
			while (set.next())
				tables.add(set.getString(1));
		} catch (Exception e) {
		}
		return tables;
	}

	@Override
	public boolean updateTables(String version, SQLParser... parsers) {
		boolean update = false, result = update;
		if (!this.sql.execute("CREATE TABLE IF NOT EXISTS " + getPrefix()
				+ "information(param VARCHAR(16),value VARCHAR(16),CONSTRAINT " + getPrefix()
				+ "PK_information PRIMARY KEY (param));"))
			return false;
		List<String> tlist = new ArrayList<>();
		try {
			this.sql.execute("SET FOREIGN_KEY_CHECKS=0;");
			ResultSet set = this.sql.query("SELECT value FROM " + getPrefix() + "information WHERE `param`='version';");
			String v = "";
			if (set.next())
				v = set.getString(1);
			if (!v.equals(version)) {
				update = true;
				for (String table : selectTables()) {
					if (!table.equals(getPrefix() + "information") && table.startsWith(getPrefix())) {
						if (!this.sql.execute("CREATE TABLE " + table + "_old AS SELECT * FROM " + table + ";"))
							throw new Exception("Error while parsing tables to old tables");
						tlist.add(table);
					}
				}
				String remove = "";
				for (String table : tlist)
					remove = String.valueOf(remove) + (remove.isEmpty() ? "" : ",") + table;
				if (!remove.isEmpty())
					this.sql.execute("DROP TABLE IF EXISTS " + remove + ";");
				for (Table table : this.getTables())
					if (!createTable(table)) {
						throw new Exception("Error while creating table: " + table.getName());
					}
				for (SQLParser parser : parsers) {
					if (parser.getWhen() == When.TABLES_CREATED && !parser.parse(this.sql, v, this.getPrefix())) {
						throw new Exception("Error while parsing");
					}
				}
				for (String table : tlist) {
					List<String> oldt = new ArrayList<>();
					ResultSet cfields = this.sql.query("DESC " + table + ";", new Data[0]);
					if (cfields != null) {
						for (; cfields.next(); oldt.add(cfields.getString("Field")))
							;
						List<String> transfer = new ArrayList<>();
						ResultSet tfields = this.sql.query("DESC " + table + "_old;", new Data[0]);
						while (tfields.next()) {
							String column = tfields.getString("Field");
							if (oldt.contains(column))
								transfer.add(column);
						}
						if (!transfer.isEmpty()) {
							String fields = "";
							for (String field : transfer)
								fields = String.valueOf(fields) + (fields.isEmpty() ? "" : ",") + field;
							if (fields.isEmpty() || !this.sql.execute("INSERT INTO " + table + "(" + fields
									+ ") SELECT " + fields + " FROM " + table + "_old;"))
								;
						}
					}
				}
				for (SQLParser parser : parsers) {
					if (parser.getWhen() == When.DATA_REIMPORTED && !parser.parse(this.sql, v, this.getPrefix())) {
						throw new Exception("Error while parsing");
					}
				}
			}
			if (!v.isEmpty()) {
				this.sql.execute("UPDATE " + getPrefix() + "information SET `value`=? WHERE `param`=?;",
						new Data[] { new Data(12, version), new Data(12, "version") });
			} else {
				this.sql.execute("INSERT INTO " + getPrefix() + "information VALUES(?,?);",
						new Data[] { new Data(12, "version"), new Data(12, version) });
			}
			result = true;
		} catch (Exception e) {
			PandaPluginClass.getSimpleLogger().sendError(
					"An error has occurred while updating the database. Report this issue and use your backup.");
			e.printStackTrace();
		} finally {
			if (update) {
				String remove = "";
				for (String table : selectTables()) {
					if (table.endsWith("_old"))
						remove = String.valueOf(remove) + (remove.isEmpty() ? "" : ",") + table;
				}
				if (!remove.isEmpty())
					this.sql.execute("DROP TABLE IF EXISTS " + remove + ";");
			}
			this.sql.execute("SET FOREIGN_KEY_CHECKS=1;");
		}
		return result;
	}

	@Override
	public boolean createTable(Table table) {
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table.getName());
		sb.append(table.getColumns().stream().map(c -> {
			StringBuilder csb = new StringBuilder(c.getName());
			Column column = c.getReference() != null ? c.getReference() : c;
			csb.append(" ").append(column.getType());
			if (column.getUnsigned())
				csb.append(" UNSIGNED");
			if (column.getAutoIncrement())
				csb.append(" AUTO_INCREMENT");
			if (column.getNotNull())
				csb.append(" NOT NULL");
			if (column.getDefault() != null && !column.getDefault().isEmpty())
				csb.append(" DEFAULT ").append(column.getDefault());
			return csb.toString();
		}).collect(Collectors.joining(",", "(", "")));
		if (table.getPrimaryColumns().size() != 0) {
			sb.append(", CONSTRAINT PK_").append(table.getName()).append(" PRIMARY KEY")
					.append(table.getPrimaryColumns().stream().map(column -> column.getName())
							.collect(Collectors.joining(", ", "(", ")")));
		}
		sb.append(table.getForeignConstraints().stream().map(foreignc -> {
			StringBuilder fsb = new StringBuilder();
			fsb.append(", CONSTRAINT FK_").append(foreignc.getOrigins().get(0).getTable().getName()).append("_")
					.append(foreignc.getReferences().get(0).getTable().getName()).append(" FOREIGN KEY ");
			fsb.append(foreignc.getOrigins().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			fsb.append(" REFERENCES ").append(foreignc.getReferences().get(0).getTable().getName());
			fsb.append(foreignc.getReferences().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			fsb.append(" ON DELETE CASCADE");
			return fsb.toString();
		}).collect(Collectors.joining()));
		sb.append(table.getUniqueConstraints().stream().map(uniquec -> {
			StringBuilder usb = new StringBuilder();
			usb.append(", CONSTRAINT UK_").append(table.getName());
			usb.append(
					uniquec.getUniques().stream().map(column -> "_" + column.getName()).collect(Collectors.joining()));
			usb.append(" UNIQUE ");
			usb.append(uniquec.getUniques().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			return usb.toString();
		}).collect(Collectors.joining()));
		if (!this.sql.execute(sb.append(");").toString()))
			return false;
		if (table.getIndexedColumns().size() != 0) {
			sb.setLength(0);
			sb.append("CREATE INDEX ").append(table.getName() + "_index ON " + table.getName());
			sb.append(table.getIndexedColumns().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			if (!this.sql.execute(sb.append(";").toString()))
				return false;
		}
		return true;
	}

	@Override
	public boolean truncateTable(Table table) {
		return this.sql.execute("TRUNCATE TABLE " + table.getName() + ";");
	}

	@Override
	public boolean dropTable(Table table) {
		return this.sql.execute("DROP TABLE IF EXISTS " + table.getName() + ";");
	}

	@Override
	public boolean setForeignKeyCheck(boolean enabled) {
		return this.sql.execute("SET FOREIGN_KEY_CHECKS = " + (enabled ? '1' : '0') + ";");
	}

	@Override
	public ResultSet selectDistinct(boolean distinct, List<Table> allcolumns, List<Column> columns, List<Join> joins,
			Condition... conditions) {
		List<Data> data = new ArrayList<>();
		List<Table> tables = new ArrayList<>();
		StringBuilder sb = new StringBuilder("SELECT ");
		if (distinct) {
			sb.append("DISTINCT ");
		}
		sb.append(allcolumns.stream().map(table -> {
			if (!tables.contains(table)) {
				tables.add(table);
			}
			return table.getName() + ".*";
		}).collect(Collectors.joining(",")));
		sb.append(columns.stream().map(column -> {
			if (!tables.contains(column.getTable())) {
				tables.add(column.getTable());
			}
			return column.getTable().getName() + ".*" + column.getName();
		}).collect(Collectors.joining(",")));
		sb.append(" FROM ");
		sb.append(tables.stream().filter(table -> {
			for (Join join : joins) {
				if (join.getAppend().getTable().getName().equals(table.getName())) {
					return false;
				}
			}
			return true;
		}).map(table -> table.getName()).collect(Collectors.joining(",")));
		sb.append(joins.stream().map(join -> " " + join.toString()).collect(Collectors.joining()));
		if (conditions.length != 0) {
			sb.append(" WHERE ");
			sb.append(Arrays.stream(conditions).map(condition -> {
				data.addAll(condition.getData());
				return condition.toString();
			}).collect(Collectors.joining(" ")));
		}
		return this.sql.query(sb.append(";").toString(), data);
	}

	@Override
	public boolean insertMultiple(Table table, List<Map<Column, Data>> entries) {
		List<Data> data = new ArrayList<>();
		entries.forEach(values -> data.addAll(values.values()));
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(table.getName());
		// INSERT INTO table
		sb.append(entries.get(0).keySet().stream().map(column -> column.getName())
				.collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2)
		sb.append(" VALUES ");
		// INSERT INTO table(column1,column2) VALUES
		sb.append(entries.stream()
				.map(values -> Arrays.stream(new String(new char[values.values().size()]).replace("\0", "?").split(""))
						.collect(Collectors.joining(",", "(", ")")))
				.collect(Collectors.joining(",")));
		// INSERT INTO table(column1,column2) VALUES (?,?),(?,?);
		return this.sql.execute(sb.append(";").toString(), data);
	}

	@Override
	public boolean insertMultipleIgnore(Table table, List<Map<Column, Data>> entries) {
		List<Data> data = new ArrayList<>();
		entries.forEach(values -> data.addAll(values.values()));
		StringBuilder sb = new StringBuilder("INSERT IGNORE INTO ").append(table.getName());
		// INSERT INTO table
		sb.append(entries.get(0).keySet().stream().map(column -> column.getName())
				.collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2)
		sb.append(" VALUES ");
		// INSERT INTO table(column1,column2) VALUES
		sb.append(entries.stream()
				.map(values -> Arrays.stream(new String(new char[values.values().size()]).replace("\0", "?").split(""))
						.collect(Collectors.joining(",", "(", ")")))
				.collect(Collectors.joining(",")));
		// INSERT INTO table(column1,column2) VALUES (?,?),(?,?);
		return this.sql.execute(sb.append(";").toString(), data);
	}

	@Override
	public int insertWithID(Table table, Map<Column, Data> values) {
		List<Data> data = new ArrayList<>(values.values());
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(table.getName());
		// INSERT INTO table
		sb.append(values.keySet().stream().map(column -> column.getName()).collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2)
		sb.append(" VALUES ");
		// INSERT INTO table(column1,column2) VALUES
		sb.append(Arrays.stream(new String(new char[values.values().size()]).replace("\0", "?").split(""))
				.collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2) VALUES (?,?);
		return this.sql.executeWithId(sb.append(";").toString(), data);
	}

	@Override
	public boolean update(Table table, Map<Column, Data> values, Condition... conditions) {
		List<Data> data = new ArrayList<>(values.values());
		StringBuilder sb = new StringBuilder("UPDATE ").append(table.getName()).append(" SET ");
		// UPDATE table SET
		sb.append(values.keySet().stream().map(column -> column.getName() + "=?").collect(Collectors.joining(",")));
		// UPDATE table SET column1=?,column2=?
		if (conditions.length != 0) {
			sb.append(" WHERE ");
			// UPDATE table SET column1=?,column2=? WHERE
			sb.append(Arrays.stream(conditions).map(condition -> {
				data.addAll(condition.getData());
				return condition.toString();
			}).collect(Collectors.joining(" ")));
			// UPDATE table SET column1=?,column2=? WHERE column3=? AND column4=?
		}
		return this.sql.execute(sb.append(";").toString(), data);
	}

	@Override
	public boolean insertOrUpdate(Table table, Map<Column, Data> values, Condition... conditions) {
		Data[] data = values.values().toArray(new Data[values.values().size()]);
		StringBuilder sb = new StringBuilder("INSERT INTO ").append(table.getName());
		// INSERT INTO table
		sb.append(values.keySet().stream().map(column -> column.getName()).collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2)
		sb.append(" VALUES ");
		// INSERT INTO table(column1,column2) VALUES
		sb.append(Arrays.stream(new String(new char[values.values().size()]).replace("\0", "?").split(""))
				.collect(Collectors.joining(",", "(", ")")));
		// INSERT INTO table(column1,column2) VALUES (?,?)
		sb.append(" ON DUPLICATE KEY UPDATE ");
		// INSERT INTO table(column1,column2) VALUES (?,?) ON DUPLICATE KEY UPDATE
		sb.append(values.keySet().stream().map(column -> {
			return column.getName() + " = VALUES (" + column.getName() + ")";
		}).collect(Collectors.joining(",")));
		// INSERT INTO table(column1,column2) VALUES (?,?) ON DUPLICATE KEY UPDATE
		// column3=VALUES(column3)
		return this.sql.execute(sb.append(";").toString(), data);
	}

	@Override
	public boolean delete(Table table, Condition... conditions) {
		List<Data> data = new ArrayList<>();
		StringBuilder sb = new StringBuilder("DELETE FROM ").append(table.getName());
		if (conditions.length != 0) {
			sb.append(" WHERE ");
			sb.append(Arrays.stream(conditions).map(condition -> {
				data.addAll(condition.getData());
				return condition.toString();
			}).collect(Collectors.joining(" ")));
		}
		return this.sql.execute(sb.append(";").toString(), data);
	}
}
