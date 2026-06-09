package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.Parameter;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class IsNotNullCondition implements Condition {

	private static final String FORMAT = "%s IS NOT NULL";
	private static final String COLUMN_FORMAT = "%s%s.%s";

	private Column column;

	@Override
	public List<Parameter> getParameters(List<Parameter> list) {
		return list;
	}

	@Override
	public String toString(SQLConnection sqlConnection) {
		return String.format(FORMAT,
				String.format(COLUMN_FORMAT, sqlConnection.getPrefix(), column.getTable().getName(), column.getName()));
	}

	public static IsNotNullCondition inst(Column column) {
		return new IsNotNullCondition(column);
	}

}
