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
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.DataModel.Table;

public class SQLiteDatabase extends MySQLDatabase {
	public SQLiteDatabase(ConnectionData data, int version) throws ClassNotFoundException {
		super(data, version);
	}

	public SQLiteDatabase(ConnectionData data, int version, String prefix) throws ClassNotFoundException {
		super(data, version, prefix);
	}

	@Override
	public List<String> selectTables() {
		List<String> tables = new ArrayList<>();
		try {
			ResultSet set = this.sql
					.query("SELECT name FROM this.sqlite_master WHERE type='table' AND name NOT LIKE 'this.sqlite_%';");
			while (set.next()) {
				tables.add(set.getString(1));
			}
		} catch (Exception e) {
			try {
				ResultSet set = this.sql
						.query("SELECT name FROM sqlite_schema WHERE type='table' AND name NOT LIKE 'sqlite_%';");
				while (set.next()) {
					tables.add(set.getString(1));
				}
			} catch (Exception e2) {
			}
		}
		return tables;
	}

	@Override
	public boolean updateTables(String version, SQLParser... parsers) {
		boolean update = false, result = update;
		if (!this.sql.execute(
				"CREATE TABLE IF NOT EXISTS information(param VARCHAR(16),value VARCHAR(16),CONSTRAINT PK_information PRIMARY KEY (param));"))
			return false;
		List<String> tlist = new ArrayList<>();
		try {
			if (!this.sql.execute("BEGIN IMMEDIATE TRANSACTION;"))
				throw new Exception();
			this.sql.execute("PRAGMA foreign_keys=off;");
			ResultSet set = this.sql.query("SELECT value FROM information WHERE `param`='version';");
			String v = "";
			if (set.next()) {
				v = set.getString(1);
			}
			set.close();
			if (!v.equals(version)) {
				update = true;
				List<String> check = selectTables();
				for (String name : check) {
					if (!name.equals("information")) {
						if (!this.sql.execute("CREATE TABLE " + name + "_old AS SELECT * FROM " + name + ";")) {
							throw new Exception();
						}
						if (!this.sql.execute("DROP TABLE IF EXISTS " + name + ";")) {
							throw new Exception();
						}
						tlist.add(name);
					}
				}
				for (Table table : getTables()) {
					if (!createTable(table)) {
						throw new Exception("Unable to create table " + table.getName());
					}
				}
				for (SQLParser parser : parsers) {
					if (parser.getWhen() == When.TABLES_CREATED && !parser.parse(this.sql, v, "")) {
						throw new Exception("Error while parsing");
					}
				}
				for (String table : tlist) {
					ResultSet cfields = this.sql.query("SELECT name FROM pragma_table_info(\"" + table
							+ "\") WHERE name IN (SELECT name FROM pragma_table_info(\"" + table + "_old\"));");
					if (cfields != null) {
						String fields = "";
						while (cfields.next())
							fields += (fields.isEmpty() ? "" : ",") + cfields.getString("name");
						cfields.close();
						if (fields.isEmpty() || !this.sql.execute("INSERT INTO " + table + "(" + fields + ") SELECT "
								+ fields + " FROM " + table + "_old;"))
							;
					}
				}
				for (SQLParser parser : parsers) {
					if (parser.getWhen() == When.DATA_REIMPORTED && !parser.parse(this.sql, v, "")) {
						throw new Exception("Error while parsing");
					}
				}
			}
			if (!v.isEmpty())
				this.sql.execute("UPDATE information SET `value`=? WHERE `param`=?;", new Data(12, version),
						new Data(12, "version"));
			else
				this.sql.execute("INSERT INTO information VALUES(?,?);", new Data(12, "version"),
						new Data(12, version));
			result = true;
			if (!this.sql.execute("COMMIT TRANSACTION;"))
				throw new Exception();
		} catch (Exception e) {
			this.sql.execute("ROLLBACK TRANSACTION;");
			PandaPluginClass.getSimpleLogger().sendError(
					"An error has occurred while updating the database. Report this issue and use your backup.");
			e.printStackTrace();
		} finally {
			if (update)
				try {
					if (!this.sql.execute("BEGIN IMMEDIATE TRANSACTION;"))
						throw new Exception();
					List<String> check = selectTables();
					for (String table : check)
						if (table.endsWith("_old") && !this.sql.execute("DROP TABLE IF EXISTS " + table + ";"))
							throw new Exception();
					if (!this.sql.execute("COMMIT TRANSACTION;"))
						throw new Exception();
				} catch (Exception e1) {
					this.sql.execute("ROLLBACK TRANSACTION;");
				}
			this.sql.execute("PRAGMA foreign_keys=on;");
		}
		return result;
	}

	@Override
	public boolean setForeignKeyCheck(boolean enabled) {
		return this.sql.execute("PRAGMA foreign_keys=" + (enabled ? "on" : "off") + ";");
	}

	@Override
	public boolean createTable(Table table) {
		StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(table.getName());
		sb.append(table.getColumns().stream().map(c -> {
			StringBuilder csb = new StringBuilder(c.getName());
			Column column = c.getReference() != null ? c.getReference() : c;
			if (column.getAutoIncrement()) {
				csb.append(" INTEGER PRIMARY KEY AUTOINCREMENT");
				if (column.getNotNull()) {
					csb.append(" NOT NULL");
				}
			} else {
				csb.append(" ").append(column.getType());
				if (column.getUnsigned()) {
					csb.append(" UNSIGNED");
				}
				if (column.getNotNull()) {
					csb.append(" NOT NULL");
				}
				if (column.getDefault() != null && !column.getDefault().isEmpty()) {
					csb.append(" DEFAULT ").append(column.getDefault());
				}
			}
			return csb.toString();
		}).collect(Collectors.joining(", ", "(", "")));
		List<Column> pkeys = new ArrayList<>();
		for (Column column : table.getPrimaryColumns()) {
			if (!column.getAutoIncrement()) {
				pkeys.add(column);
			}
		}
		if (pkeys.size() != 0) {
			sb.append(", PRIMARY KEY");
			sb.append(pkeys.stream().map(key -> key.getName()).collect(Collectors.joining(", ", "(", ")")));
		}
		sb.append(table.getForeignConstraints().stream().map(foreignc -> {
			StringBuilder fsb = new StringBuilder();
			fsb.append(", FOREIGN KEY ");
			fsb.append(foreignc.getOrigins().stream().map(origin -> origin.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			fsb.append(" REFERENCES ").append(foreignc.getReferences().get(0).getTable().getName());
			fsb.append(foreignc.getReferences().stream().map(reference -> reference.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			fsb.append(" ON DELETE CASCADE");
			return fsb.toString();
		}).collect(Collectors.joining()));
		sb.append(table.getUniqueConstraints().stream().map(uniquec -> {
			StringBuilder usb = new StringBuilder();
			usb.append(", UNIQUE ");
			usb.append(uniquec.getUniques().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			return usb.toString();
		}).collect(Collectors.joining()));
		if (!this.sql.execute(sb.append(");").toString())) {
			return false;
		}
		if (table.getIndexedColumns().size() != 0) {
			sb.setLength(0);
			sb.append("CREATE INDEX ").append(table.getName() + "_index ON " + table.getName());
			sb.append(table.getIndexedColumns().stream().map(key -> key.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			if (!this.sql.execute(sb.append(";").toString())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean insertOrUpdate(Table table, Map<Column, Data> values, Condition... conditions) {
		if (insert(table, values))
			return true;
		return update(table, values, conditions);
	}

	@Override
	public boolean insertMultipleIgnore(Table table, List<Map<Column, Data>> entries) {
		List<Data> data = new ArrayList<>();
		entries.forEach(values -> data.addAll(values.values()));
		StringBuilder sb = new StringBuilder("INSERT OR IGNORE INTO ").append(table.getName());
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
}