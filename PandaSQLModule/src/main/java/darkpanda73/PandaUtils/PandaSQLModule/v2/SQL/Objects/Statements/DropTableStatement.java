package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class DropTableStatement extends Statement {

	private Table table;

	public DropTableStatement(Table table) {
		this.table = table;
	}

	public DropTableStatement setTable(Table table) {
		this.table = table;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public static DropTableStatement inst(Table table) {
		return new DropTableStatement(table);
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, DropTableStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("DROP TABLE " + sqlConnection.getPrefix() + statement.table.getName());

		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

}