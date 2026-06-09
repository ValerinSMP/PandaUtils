package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Conditions;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements.Statement.Parameter;

public interface Condition {

	public default List<Parameter> getParameters() {
		return this.getParameters(new ArrayList<>());
	}

	public List<Parameter> getParameters(List<Parameter> list);

	public String toString(SQLConnection sqlConnection);

}
