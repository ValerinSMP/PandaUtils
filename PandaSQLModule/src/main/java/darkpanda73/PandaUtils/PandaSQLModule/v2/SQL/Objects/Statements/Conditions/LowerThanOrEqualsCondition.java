package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class LowerThanOrEqualsCondition extends ComparisonCondition {

	public LowerThanOrEqualsCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return "<=";
	}

	public static LowerThanOrEqualsCondition inst(Object firstObject, Object secondObject) {
		return new LowerThanOrEqualsCondition(firstObject, secondObject);
	}

}
