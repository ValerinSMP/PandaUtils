package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Table;

public class CreateTableStatement extends Statement {

	private Table table;

	public CreateTableStatement(Table table) {
		this.table = table;
	}

	public CreateTableStatement setTable(Table table) {
		this.table = table;
		return this;
	}

	public PreparedStatement build(SQLConnection sqlConnection) {
		return buildStatement(sqlConnection, this);
	}

	public static CreateTableStatement inst(Table table) {
		return new CreateTableStatement(table);
	}

	private static PreparedStatement buildStatement(SQLConnection sqlConnection, CreateTableStatement statement) {
		PreparedStatement preparedStatement = new PreparedStatement();

		StringBuilder builder = new StringBuilder();

		builder.append("CREATE TABLE IF NOT EXISTS ");

		builder.append(sqlConnection.getPrefix() + statement.table.getName());

		builder.append(
				statement.table.getColumns().stream().map(Column::toString).collect(Collectors.joining(", ", "(", "")));

		List<Column> pkeys = new ArrayList<>();
		for (Column column : statement.table.getColumns()) {
			if (column.isPrimary()) {
				pkeys.add(column);
			}
		}

		if (pkeys.size() != 0) {
			builder.append(", PRIMARY KEY");

			builder.append(pkeys.stream().map(key -> key.getName()).collect(Collectors.joining(", ", "(", ")")));
		}

		builder.append(statement.table.getForeignConstraints().stream().map(foreignc -> {
			StringBuilder foreignBuilder = new StringBuilder();
			foreignBuilder.append(", FOREIGN KEY ");
			foreignBuilder.append(foreignc.getColumns().stream().map(origin -> origin.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			foreignBuilder.append(" REFERENCES ")
					.append(sqlConnection.getPrefix() + foreignc.getReferenceColumns().get(0).getTable().getName());
			foreignBuilder.append(foreignc.getReferenceColumns().stream().map(reference -> reference.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			return foreignBuilder.toString();
		}).collect(Collectors.joining()));

		builder.append(statement.table.getUniqueConstraints().stream().map(uniquec -> {
			StringBuilder usb = new StringBuilder();
			usb.append(", UNIQUE ");
			usb.append(uniquec.getColumns().stream().map(column -> column.getName())
					.collect(Collectors.joining(", ", "(", ")")));
			return usb.toString();
		}).collect(Collectors.joining()));

		builder.append(")");

		preparedStatement.setStatement(builder.toString());

		return preparedStatement;
	}

}