package darkpanda73.PandaUtils.PandaPlugin.Exceptions;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class ReportResult extends Throwable {

	private static final long serialVersionUID = 2721177721138939322L;

	private List<ReportError> errors = new ArrayList<>();
	private boolean success = true;
	private boolean critical = false;

	public void addErrors(ReportError... errors) {
		for (ReportError error : errors) {
			this.errors.add(error);

			if (!this.critical && error.isCritical()) {
				this.critical = true;
			}
		}
		this.success = false;
	}

	@AllArgsConstructor
	@Data
	@Setter(lombok.AccessLevel.NONE)
	@EqualsAndHashCode(callSuper = true)
	public static class ReportError extends Throwable {

		private static final long serialVersionUID = 5778500048232543480L;

		private String message;

		private boolean critical = true;

		private Throwable cause;

		public ReportError(String message) {
			this(message, true, null);
		}

		public ReportError(String message, boolean critical) {
			this(message, critical, null);
		}

		@Override
		public String toString() {
			return message;
		}

	}

}