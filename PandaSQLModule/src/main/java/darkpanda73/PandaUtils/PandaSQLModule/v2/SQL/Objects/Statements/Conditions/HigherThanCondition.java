package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class HigherThanCondition extends ComparisonCondition {

	public HigherThanCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return ">";
	}

	public static HigherThanCondition inst(Object firstObject, Object secondObject) {
		return new HigherThanCondition(firstObject, secondObject);
	}

}
