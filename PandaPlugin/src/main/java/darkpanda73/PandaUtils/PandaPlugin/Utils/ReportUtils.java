package darkpanda73.PandaUtils.PandaPlugin.Utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;

public class ReportUtils {

	public static ReportResult generateResult(Object obj, Method method, Object... parameters) {
		return generateResult(result -> {
			try {
				if (method.getParameterCount() > 0 && method.getParameters()[0].getType() == ReportResult.class) {
					Object[] newParameters = new Object[parameters.length + 1];

					newParameters[0] = result;
					for (int i = 0; i < parameters.length; i++) {
						newParameters[i + 1] = parameters[i];
					}
					JavaUtils.executeMethod(obj, method, newParameters);
				} else {
					JavaUtils.executeMethod(obj, method, parameters);
				}
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ReportError("Unable to run method", true, e.getCause() != null ? e.getCause() : e);
			}
		});
	}

	public static ReportResult generateResult(Action action) {
		ReportResult result = new ReportResult();
		try {
			action.execute(result);
		} catch (Throwable e) {
			if (e.getCause() != null && e.getCause() != e) {
				e = e.getCause();
			}

			if (e instanceof ReportResult) {
				return (ReportResult) e;
			} else if (e instanceof ReportError) {
				result.addErrors((ReportError) e);
			} else {
				result.addErrors(new ReportError(e.getMessage(), true, e));
			}
		}
		return result;
	}

	public static interface Action {

		public abstract void execute(ReportResult result) throws Throwable;

	}

}
