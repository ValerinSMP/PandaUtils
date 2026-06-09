package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Statements;

import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableFunction;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

public abstract class Statement {

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class PreparedStatement {

		private String statement;
		private List<Parameter> parameters = new ArrayList<>();
		private FailableFunction<Throwable, ResultSet> onException;

		public PreparedStatement(String statement) {
			this.statement = statement;
		}

		public PreparedStatement(String statement, List<Parameter> parameters) {
			this.statement = statement;
			this.parameters = parameters;
		}

		public void addParameter(int type, Object data) {
			parameters.add(Parameter.inst(type, data));
		}

		public void addParameter(Parameter parameter) {
			parameters.add(parameter);
		}

	}

	@AllArgsConstructor
	@Getter
	@ToString
	public static class Parameter {

		private int type;
		private Object data;

		public static Parameter inst(int type, Object data) {
			return new Parameter((data != null ? type : Types.NULL), data);
		}

	}

}
