package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class HigherThanOrEqualsCondition extends ComparisonCondition {

	public HigherThanOrEqualsCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return ">=";
	}

	public static HigherThanOrEqualsCondition inst(Object firstObject, Object secondObject) {
		return new HigherThanOrEqualsCondition(firstObject, secondObject);
	}

}
