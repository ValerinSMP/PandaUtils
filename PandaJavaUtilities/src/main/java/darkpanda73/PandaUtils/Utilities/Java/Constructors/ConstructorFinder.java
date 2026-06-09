package darkpanda73.PandaUtils.Utilities.Java.Constructors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Consumer;
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
public class ConstructorFinder {

	private @NonNull @Setter(lombok.AccessLevel.NONE) @ToString.Include Class<?> fromClass;

	private @ToString.Include Class<?>[] parameters = null;
	private @ToString.Include Visibility visibility = null;
	private @ToString.Include Function<Constructor<?>, Boolean> checkingFunction = null;

	private boolean recursive = true;

	public static ConstructorFinder forClass(Class<?> fromClass) {
		return new ConstructorFinder(fromClass);
	}

	public ConstructorFinder setParameters(Class<?>... parameters) {
		this.parameters = parameters;
		return this;
	}

	public ConstructorResult findSafely() {
		return new ConstructorResult(findRecursively(fromClass).orElse(null));
	}

	public ConstructorResult find() {
		ConstructorResult field = findSafely();

		if (field.isEmpty()) {
			throw new NullPointerException("Unable to find field: " + this.toString());
		}

		return field;
	}

	private Optional<Constructor<?>> findRecursively(Class<?> currentClass) {
		Optional<Constructor<?>> foundConstructor = Arrays.stream(currentClass.getDeclaredConstructors())
				.filter(constructor -> {
					if (parameters != null) {
						if (constructor.getParameterTypes().length != parameters.length) {
							return false;
						}

						for (int i = 0; i < parameters.length; i++) {
							if (!constructor.getParameterTypes()[i].isAssignableFrom(parameters[i])) {
								return false;
							}
						}

						if (visibility != null && !visibility.check(constructor)) {
							return false;
						}

						if (checkingFunction != null && !checkingFunction.apply(constructor)) {
							return false;
						}
					}

					return true;
				}).findFirst();

		return foundConstructor.isPresent() ? foundConstructor
				: (recursive && currentClass.getSuperclass() != null ? findRecursively(currentClass.getSuperclass())
						: Optional.empty());
	}

	@AllArgsConstructor
	public static enum Visibility {
		ANY((field) -> true), PRIVATE((field) -> Modifier.isPrivate(field.getModifiers())),
		PUBLIC((field) -> Modifier.isPublic(field.getModifiers())),
		PROTECTED((field) -> Modifier.isProtected(field.getModifiers())), PACKAGE((field) -> false);

		private Function<Constructor<?>, Boolean> checker;

		public boolean check(Constructor<?> constructor) {
			return this.checker.apply(constructor);
		}
	}

	@AllArgsConstructor
	public static class ConstructorResult {

		@Getter
		private Constructor<?> constructor;

		public boolean isEmpty() {
			return this.constructor == null;
		}

		public boolean isPresent() {
			return this.constructor != null;
		}

		public <T> T newInstanceSafelyWithStackTrace(Object... parameters) {
			try {
				return newInstance(parameters);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		}

		public <T> T newInstanceSafely(Object... parameters) {
			try {
				return newInstance(parameters);
			} catch (Throwable e) {
				return null;
			}
		}

		@SuppressWarnings("unchecked")
		public <T> T newInstance(Object... parameters)
				throws IllegalAccessException, InstantiationException, InvocationTargetException {
			if (isEmpty()) {
				return null;
			}

			boolean isPrivate = Modifier.isPrivate(constructor.getModifiers())
					|| Modifier.isProtected(constructor.getModifiers());
			try {
				if (isPrivate) {
					constructor.setAccessible(true);
				}

				return (T) constructor.newInstance(parameters);
			} finally {
				if (isPrivate) {
					constructor.setAccessible(false);
				}
			}
		}

		public void ifPresent(Consumer<ConstructorResult> fieldResult) {
			if (isPresent()) {
				fieldResult.accept(this);
			}
		}

	}

}
