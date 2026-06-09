package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class EqualsCondition extends ComparisonCondition {

	public EqualsCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return "=";
	}

	public static EqualsCondition inst(Object firstObject, Object secondObject) {
		return new EqualsCondition(firstObject, secondObject);
	}

}
