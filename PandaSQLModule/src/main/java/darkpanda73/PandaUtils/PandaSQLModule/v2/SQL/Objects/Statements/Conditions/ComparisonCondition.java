package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.Parameter;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Tables.Column;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils.TypeUtilities;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public abstract class ComparisonCondition implements Condition {

	private static final String FORMAT = "%s %s %s";
	private static final String COLUMN_FORMAT = "%s%s.%s";

	private Object firstObject;
	private Object secondObject;

	public abstract String getComparisonSymbol();

	@Override
	public List<Parameter> getParameters(List<Parameter> list) {
		Integer type = firstObject instanceof Column ? ((Column) firstObject).getType()
				: (secondObject instanceof Column ? ((Column) secondObject).getType() : null);

		if (!(firstObject instanceof Column)) {
			list.add(Parameter.inst(type != null ? type : TypeUtilities.findTypeByObject(firstObject), firstObject));
		}

		if (!(secondObject instanceof Column)) {
			list.add(Parameter.inst(type != null ? type : TypeUtilities.findTypeByObject(secondObject), secondObject));
		}

		return list;
	}

	@Override
	public String toString(SQLConnection sqlConnection) {
		String firstObjectString = firstObject instanceof Column
				? String.format(COLUMN_FORMAT, sqlConnection.getPrefix(), ((Column) firstObject).getTable().getName(),
						((Column) firstObject).getName())
				: "?";
		String secondObjectString = secondObject instanceof Column
				? String.format(COLUMN_FORMAT, sqlConnection.getPrefix(), ((Column) secondObject).getTable().getName(),
						((Column) secondObject).getName())
				: "?";

		return String.format(FORMAT, firstObjectString, getComparisonSymbol(), secondObjectString);
	}

}
