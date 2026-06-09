package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

public class UpdateStatement extends Statement {

	private Table table;
	private List<Pair<Column, Parameter>> values = new ArrayList<>();
	private Condition condition;

	public UpdateStatement(Table table) {
		this.table = table;
	}

	public UpdateStatement addValue(Column column, Object value) {
		values.add(Pair.of(column,
				value instanceof Parameter ? (Parameter) value : Parameter.inst(column.getType(), value)));
		return this;
	}

	public UpdateStatement setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public static UpdateStatement inst(Table table) {
		return new UpdateStatement(table);
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, UpdateStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("UPDATE " + sqlConnection.getPrefix() + statement.table.getName() + " SET ");

		String columnsString = statement.values.stream()
				.map(pair -> pair.getFirst().getName() + " = "
						+ (pair.getSecond() != null && pair.getSecond().getData() instanceof Column
								? ((Column) pair.getSecond().getData()).getName()
								: "?"))
				.collect(Collectors.joining(", "));
		statement.values.forEach(pair -> {
			if (pair.getSecond() == null || !(pair.getSecond().getData() instanceof Column)) {
				preparedStatement.addParameter(pair.getSecond());
			}
		});

		builder.append(columnsString);

		if (statement.condition != null) {
			builder.append(" WHERE ");

			builder.append(statement.condition.toString(sqlConnection));

			statement.condition.getParameters().forEach(preparedStatement::addParameter);
		}

		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

}