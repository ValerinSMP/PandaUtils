package darkpanda73.PandaUtils.Utilities.Java.Methods;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Data
@ToString(onlyExplicitlyIncluded = true)
@RequiredArgsConstructor
@Accessors(chain = true)
public class MethodFinder {

	private @NonNull @Setter(lombok.AccessLevel.NONE) @ToString.Include Class<?> fromClass;

	private @ToString.Include String name = null;
	private @ToString.Include Class<?> returnType = null;
	private @ToString.Include Class<?>[] params = null;
	private @ToString.Include Visibility visibility = null;
	private @ToString.Include Boolean staticAccess = null;
	private @ToString.Include Function<Method, Boolean> checkingFunction = null;

	private boolean recursive = true;

	public static MethodFinder forData(Class<?> fromClass, String name, Class<?>... params) {
		return new MethodFinder(fromClass).setName(name).setParams(params);
	}

	public static MethodFinder forClass(Class<?> fromClass) {
		return new MethodFinder(fromClass);
	}

	public MethodFinder setParams(Class<?>... params) {
		this.params = params;
		return this;
	}

	public MethodResult findSafely() {
		return new MethodResult(findRecursively(fromClass).orElse(null));
	}

	public MethodResult find() {
		MethodResult result = findSafely();

		if (result.isEmpty()) {
			throw new NullPointerException("Unable to find method: " + this.toString());
		}
		return result;
	}

	private Optional<Method> findRecursively(Class<?> currentClass) {
		Optional<Method> foundMethod = Arrays.stream(
				Boolean.TRUE.equals(getStaticAccess()) ? currentClass.getMethods() : currentClass.getDeclaredMethods())
				.filter(method -> {
					if (name != null && !method.getName().equals(name)) {
						return false;
					}

					if (returnType != null && !returnType.isAssignableFrom(method.getReturnType())) {
						return false;
					}

					if (params != null) {
						if (params.length != method.getParameterCount()) {
							return false;
						}

						for (int i = 0; i < params.length; i++) {
							if (!method.getParameters()[i].getType().isAssignableFrom(params[i])) {
								return false;
							}
						}
					}

					if (visibility != null && !visibility.check(method)) {
						return false;
					}

					if (staticAccess != null && staticAccess != Modifier.isStatic(method.getModifiers())) {
						return false;
					}

					if (checkingFunction != null && !checkingFunction.apply(method)) {
						return false;
					}

					return true;
				}).findFirst();

		return foundMethod.isPresent() ? foundMethod
				: (recursive && currentClass.getSuperclass() != null ? findRecursively(currentClass.getSuperclass())
						: Optional.empty());
	}

	@AllArgsConstructor
	public static enum Visibility {
		ANY((method) -> true), PRIVATE((method) -> Modifier.isPrivate(method.getModifiers())),
		PUBLIC((method) -> Modifier.isPublic(method.getModifiers())),
		PROTECTED((method) -> Modifier.isProtected(method.getModifiers())), PACKAGE((method) -> false);

		private Function<Method, Boolean> checker;

		public boolean check(Method field) {
			return this.checker.apply(field);
		}
	}

	@AllArgsConstructor
	public static class MethodResult {

		public static final MethodResult EMPTY = new MethodResult(null);

		@Getter
		private Method method;

		public boolean isEmpty() {
			return this.method == null;
		}

		public boolean isPresent() {
			return this.method != null;
		}

		public <T> T invokeSafelyWithStackTrace(Object origin, Object... params) {
			try {
				return invoke(origin, params);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		}

		public <T> T invokeSafely(Object origin, Object... params) {
			try {
				return invoke(origin, params);
			} catch (Throwable e) {
				return null;
			}
		}

		public <T> T invoke(Object origin, Object... params)
				throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			if (isEmpty()) {
				return null;
			}

			return MethodUtilities.executeMethod(origin, method, params);
		}

	}

}
