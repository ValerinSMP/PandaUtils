package darkpanda73.PandaUtils.Utilities.Java.Methods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableRunnable;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableSupplier;

public class MethodUtilities {

	public static void catchAndIgnore(FailableRunnable runnable) {
		try {
			runnable.run();
		} catch (Throwable e) {
			// Ignore
		}
	}

	public static <R> R catchAndIgnore(FailableSupplier<R> function) {
		try {
			return function.get();
		} catch (Throwable e) {
			// Ignore
			return null;
		}
	}

	public static Method getMethod(Class<?> clazz, String methodName, Class<?>... parameters) {
		try {
			return clazz.getMethod(methodName, parameters);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T executeMethod(Object source, Method method, Object... parameters)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		boolean isPrivate = Modifier.isPrivate(method.getModifiers());
		try {
			if (isPrivate) {
				method.setAccessible(true);
			}

			return (T) method.invoke(source, parameters);
		} finally {
			if (isPrivate) {
				method.setAccessible(false);
			}
		}
	}

}
