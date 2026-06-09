package darkpanda73.PandaUtils.Utilities.Java.Fields;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
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
public class FieldFinder {

	private @NonNull @Setter(lombok.AccessLevel.NONE) @ToString.Include Class<?> fromClass;

	private @ToString.Include String name = null;
	private @ToString.Include Class<?> type = null;
	private @ToString.Include Class<?>[] parametersInType = null;
	private @ToString.Include Visibility visibility = null;
	private @ToString.Include Function<Field, Boolean> checkingFunction = null;

	private boolean recursive = true;

	public static FieldFinder forClass(Class<?> fromClass) {
		return new FieldFinder(fromClass);
	}

	public FieldFinder setParametersInType(Class<?>... parametersInType) {
		this.parametersInType = parametersInType;
		return this;
	}

	public FieldResult findSafely() {
		return new FieldResult(findRecursively(fromClass).orElse(null));
	}

	public FieldResult find() {
		FieldResult field = findSafely();

		if (field.isEmpty()) {
			throw new NullPointerException("Unable to find field: " + this.toString());
		}

		return field;
	}

	private Optional<Field> findRecursively(Class<?> currentClass) {
		Optional<Field> foundField = Arrays.stream(currentClass.getDeclaredFields()).filter(field -> {
			if (name != null && !field.getName().equals(name)) {
				return false;
			}

			if (type != null && !field.getType().isAssignableFrom(type)) {
				return false;
			}

			if (parametersInType != null && parametersInType.length > 0) {
				if (!(field.getGenericType() instanceof ParameterizedType)) {
					return false;
				}

				ParameterizedType parameterizedType = (java.lang.reflect.ParameterizedType) field.getGenericType();

				if (parameterizedType.getActualTypeArguments() == null
						|| parameterizedType.getActualTypeArguments().length != parametersInType.length) {
					return false;
				}

				for (int i = 0; i < parametersInType.length; i++) {
					if (!(parameterizedType.getActualTypeArguments()[i] instanceof Class)
							|| !((Class<?>) parameterizedType.getActualTypeArguments()[0])
									.isAssignableFrom(parametersInType[i])) {
						return false;
					}
				}
			}

			if (visibility != null && !visibility.check(field)) {
				return false;
			}

			if (checkingFunction != null && !checkingFunction.apply(field)) {
				return false;
			}

			return true;
		}).findFirst();

		return foundField.isPresent() ? foundField
				: (recursive && currentClass.getSuperclass() != null ? findRecursively(currentClass.getSuperclass())
						: Optional.empty());
	}

	@AllArgsConstructor
	public static enum Visibility {
		ANY((field) -> true), PRIVATE((field) -> Modifier.isPrivate(field.getModifiers())),
		PUBLIC((field) -> Modifier.isPublic(field.getModifiers())),
		PROTECTED((field) -> Modifier.isProtected(field.getModifiers())), PACKAGE((field) -> false);

		private Function<Field, Boolean> checker;

		public boolean check(Field field) {
			return this.checker.apply(field);
		}
	}

	@AllArgsConstructor
	public static class FieldResult {

		@Getter
		private Field field;

		public boolean isEmpty() {
			return this.field == null;
		}

		public boolean isPresent() {
			return this.field != null;
		}

		public <T> T getValueSafelyWithStackTrace() {
			return getValueSafely(null);
		}

		public <T> T getValueSafelyWithStackTrace(Object origin) {
			try {
				return getValue(origin);
			} catch (Throwable e) {
				e.printStackTrace();
				return null;
			}
		}

		public <T> T getValueSafely() {
			return getValueSafely(null);
		}

		public <T> T getValueSafely(Object origin) {
			try {
				return getValue(origin);
			} catch (Throwable e) {
				return null;
			}
		}

		public <T> T getValue() throws IllegalArgumentException, IllegalAccessException {
			return getValue(null);
		}

		@SuppressWarnings("unchecked")
		public <T> T getValue(Object origin) throws IllegalArgumentException, IllegalAccessException {
			if (isEmpty()) {
				return null;
			}

			boolean isPrivate = Modifier.isPrivate(field.getModifiers());
			try {
				if (isPrivate) {
					field.setAccessible(true);
				}

				return (T) field.get(origin);
			} finally {
				if (isPrivate) {
					field.setAccessible(false);
				}
			}
		}

		public void setValueSafelyWithStackTrace(Object value) {
			setValueSafely(null, value);
		}

		public void setValueSafelyWithStackTrace(Object origin, Object value) {
			try {
				setValue(origin, value);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}

		public void setValueSafely(Object value) {
			setValueSafely(null, value);
		}

		public void setValueSafely(Object origin, Object value) {
			try {
				setValue(origin, value);
			} catch (Throwable e) {
			}
		}

		public void setValue(Object value) throws IllegalArgumentException, IllegalAccessException {
			setValue(null, value);
		}

		public void setValue(Object origin, Object value) throws IllegalArgumentException, IllegalAccessException {
			if (isEmpty()) {
				return;
			}

			boolean isPrivate = Modifier.isPrivate(field.getModifiers());
			try {
				if (isPrivate) {
					field.setAccessible(true);
				}

				field.set(origin, value);
			} finally {
				if (isPrivate) {
					field.setAccessible(false);
				}
			}
		}

		public void ifPresent(Consumer<FieldResult> fieldResult) {
			if (isPresent()) {
				fieldResult.accept(this);
			}
		}

	}

}
