package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.Parameter;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AndCondition implements Condition {

	private static final String FORMAT = "%s AND %s";

	private Condition firstCondition;
	private Condition secondCondition;

	@Override
	public List<Parameter> getParameters(List<Parameter> list) {
		list.addAll(firstCondition.getParameters());
		list.addAll(secondCondition.getParameters());
		return list;
	}

	@Override
	public String toString(SQLConnection sqlConnection) {
		return String.format(FORMAT, firstCondition.toString(sqlConnection), secondCondition.toString(sqlConnection));
	}

	public static AndCondition inst(Condition firstCondition, Condition secondCondition) {
		return new AndCondition(firstCondition, secondCondition);
	}

}
