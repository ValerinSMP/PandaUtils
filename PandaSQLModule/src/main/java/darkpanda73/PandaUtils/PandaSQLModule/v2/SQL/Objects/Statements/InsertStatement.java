package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;
import lombok.Getter;

public class InsertStatement extends Statement {

	private @Getter Table table;
	private @Getter List<Column> columns;
	private @Getter List<List<Parameter>> entries = new ArrayList<>();
	private @Getter Condition conditionIfExists;

	private boolean ignoreIfExists;

	public InsertStatement(Table table, List<Column> columns) {
		this.table = table;
		this.columns = columns;
	}

	public InsertStatement addEntry(Object... values) {
		List<Parameter> parameters = new ArrayList<>();
		for (int i = 0; i < values.length && i < columns.size(); i++) {
			parameters.add(Parameter.inst((values[i] != null ? columns.get(i).getType() : Types.NULL), values[i]));
		}
		entries.add(parameters);
		return this;
	}

	public InsertStatement setConditionIfExists(Condition conditionIfExists) {
		this.conditionIfExists = conditionIfExists;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public Collection<PreparedStatement> buildUpdate(SQLConnection sqlConnection) {
		List<PreparedStatement> preparedStatements = new ArrayList<>();

		entries.forEach(entry -> {
			UpdateStatement updateStatement = UpdateStatement.inst(table).setCondition(conditionIfExists);

			for (int i = 0; i < entry.size() && i < columns.size(); i++) {
				updateStatement.addValue(columns.get(i), entry.get(i));
			}

			preparedStatements.add(updateStatement.build(sqlConnection));
		});

		return preparedStatements;
	}

	public static InsertStatement inst(Table table) {
		return new InsertStatement(table, table.getColumns());
	}

	public static InsertStatement inst(Table table, String... columns) {
		return new InsertStatement(table, Arrays.stream(columns).map(columnName -> table.getColumn(columnName))
				.filter(Objects::nonNull).collect(Collectors.toList()));
	}

	public static InsertStatement inst(Table table, Column... columns) {
		return new InsertStatement(table, Arrays.asList(columns));
	}

	public static InsertStatement inst(Table table, List<Column> columns) {
		return new InsertStatement(table, columns);
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, InsertStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("INSERT ");

		if (statement.ignoreIfExists) {
			builder.append("OR IGNORE ");
		}

		builder.append("INTO ");

		builder.append(sqlConnection.getPrefix() + statement.table.getName());

		builder.append("(");

		builder.append(statement.columns.stream().map(Column::getName).collect(Collectors.joining(", ")));

		builder.append(") VALUES ");

		builder.append(
				StringUtilities.repeat(Arrays.stream(StringUtilities.repeat('?', statement.columns.size()).split(""))
						.collect(Collectors.joining(", ", "(", ")")), statement.entries.size(), ", "));

		if (statement.conditionIfExists != null && sqlConnection.isMySQLConnection()) {
			builder.append(" ON DUPLICATE KEY UPDATE ");

			builder.append(statement.columns.stream().map(column -> {
				return column.getName() + " = VALUES (" + column.getName() + ")";
			}).collect(Collectors.joining(",")));
		}

		statement.entries.forEach(entry -> entry.forEach(preparedStatement::addParameter));
		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

}