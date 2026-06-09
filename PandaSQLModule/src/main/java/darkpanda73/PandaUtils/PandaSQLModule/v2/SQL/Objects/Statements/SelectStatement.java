package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions.Condition;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class SelectStatement extends Statement {

	private Set<Column> columns = new HashSet<>();
	private Set<Table> tables = new HashSet<>();
	private List<Sort> sorts = new ArrayList<>();
	private Condition condition;
	private Integer limit;

	private boolean distinct;

	public SelectStatement addColumn(Column column) {
		columns.add(column);
		return this;
	}

	public SelectStatement addTable(Table table) {
		tables.add(table);
		return this;
	}

	public SelectStatement addSort(Sort sort) {
		sorts.add(sort);
		return this;
	}

	public SelectStatement setCondition(Condition condition) {
		this.condition = condition;
		return this;
	}

	public SelectStatement setDistinct(boolean distinct) {
		this.distinct = distinct;
		return this;
	}

	public SelectStatement setLimit(Integer limit) {
		this.limit = limit;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public static SelectStatement inst() {
		return new SelectStatement();
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, SelectStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		Set<Table> tables = new HashSet<>(statement.tables);
		statement.columns.forEach(column -> tables.add(column.getTable()));

		StringBuilder builder = new StringBuilder();

		builder.append("SELECT ");

		if (statement.distinct) {
			builder.append("DISTINCT ");
		}

		String columnsString = statement.columns.stream()
				.map(column -> sqlConnection.getPrefix() + column.getTable().getName() + "." + column.getName())
				.collect(Collectors.joining(", "));
		String tableColumnsString = statement.tables.stream()
				.map(table -> sqlConnection.getPrefix() + table.getName() + ".*").collect(Collectors.joining(", "));

		builder.append(columnsString);

		if (!columnsString.isEmpty() && !tableColumnsString.isEmpty()) {
			builder.append(", ");
		}

		builder.append(tableColumnsString);

		builder.append(" FROM ");

		String tablesString = tables.stream().map(table -> sqlConnection.getPrefix() + table.getName())
				.collect(Collectors.joining(","));

		builder.append(tablesString);

		if (statement.condition != null) {
			builder.append(" WHERE ");

			builder.append(statement.condition.toString(sqlConnection));

			statement.condition.getParameters().forEach(preparedStatement::addParameter);
		}

		if (!statement.sorts.isEmpty()) {
			builder.append(" ORDER BY ");

			builder.append(statement.sorts.stream()
					.map(sort -> sqlConnection.getPrefix() + sort.getColumn().getTable().getName() + "."
							+ sort.getColumn().getName() + " " + sort.getDirection().name())
					.collect(Collectors.joining(", ")));
		}

		if (statement.limit != null) {
			builder.append(" LIMIT ");

			builder.append(String.valueOf(statement.limit));
		}

		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

	@AllArgsConstructor
	@NoArgsConstructor
	@Data
	public static class Sort {

		public static enum Direction {
			ASC, DESC
		}

		private Column column;
		private Direction direction;

		public static Sort inst(Column column, Direction direction) {
			return new Sort(column, direction);
		}

	}

}