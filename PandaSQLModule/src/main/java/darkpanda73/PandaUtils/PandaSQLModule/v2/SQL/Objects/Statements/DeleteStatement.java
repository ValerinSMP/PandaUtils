package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class DeleteStatement extends Statement {

	private Table table;
	private Condition condition;

	public DeleteStatement(Table table) {
		this.table = table;
	}

	public DeleteStatement setTable(Table table) {
		this.table = table;
		return this;
	}

	public DeleteStatement setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public static DeleteStatement inst(Table table) {
		return new DeleteStatement(table);
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, DeleteStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("DELETE FROM " + sqlConnection.getPrefix() + statement.table.getName());

		if (statement.condition != null) {
			builder.append(" WHERE ");

			builder.append(statement.condition.toString(sqlConnection));

			statement.condition.getParameters().forEach(preparedStatement::addParameter);
		}

		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

}