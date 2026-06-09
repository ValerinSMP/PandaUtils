package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class LowerThanCondition extends ComparisonCondition {

	public LowerThanCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return "<";
	}

	public static LowerThanCondition inst(Object firstObject, Object secondObject) {
		return new LowerThanCondition(firstObject, secondObject);
	}

}
