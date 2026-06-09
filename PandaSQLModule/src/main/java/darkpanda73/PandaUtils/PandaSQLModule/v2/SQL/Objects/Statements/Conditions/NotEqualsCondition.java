package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

public class NotEqualsCondition extends ComparisonCondition {

	public NotEqualsCondition(Object firstObject, Object secondObject) {
		super(firstObject, secondObject);
	}

	@Override
	public String getComparisonSymbol() {
		return "<>";
	}

	public static NotEqualsCondition inst(Object firstObject, Object secondObject) {
		return new NotEqualsCondition(firstObject, secondObject);
	}

}
