package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.util.ArrayList;
import java.util.List;

public class ManualQueryStatement extends Statement {

	private String statement;
	private List<Parameter> parameters = new ArrayList<>();

	public ManualQueryStatement(String statement) {
		this.statement = statement;
	}

	public PreparedStatement build() {
		return new PreparedStatement(statement, parameters);
	}

	public static ManualQueryStatement inst(String statement) {
		return new ManualQueryStatement(statement);
	}

}