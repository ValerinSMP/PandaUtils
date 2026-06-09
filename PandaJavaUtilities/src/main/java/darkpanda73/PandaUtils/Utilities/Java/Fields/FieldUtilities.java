package darkpanda73.PandaUtils.Utilities.Java.Fields;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FieldUtilities {

	public static List<Field> find(Class<?> clazz) {
		List<Field> fields = new ArrayList<>();

		Arrays.stream(clazz.getDeclaredFields()).forEach(fields::add);

		if (clazz.getSuperclass() != null) {
			find(clazz.getSuperclass()).forEach(fields::add);
		}

		return fields;
	}

	public static List<Field> findByType(Class<?> clazz, Class<?> type) {
		if (clazz.getName().startsWith("java.")) {
			return Collections.emptyList();
		}

		List<Field> fields = new ArrayList<>();

		Arrays.stream(clazz.getDeclaredFields()).filter(field -> type.isAssignableFrom(field.getType()))
				.forEach(fields::add);

		Arrays.stream(clazz.getDeclaredClasses()).forEach(dClazz -> fields.addAll(findByType(dClazz, type)));

		if (clazz.getSuperclass() != null) {
			findByType(clazz.getSuperclass(), type).forEach(fields::add);
		}

		return fields;
	}

	public static List<Field> findByAnnotation(Class<?> clazz, Class<? extends Annotation> annotation) {
		List<Field> fields = new ArrayList<>();

		Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(annotation))
				.forEach(fields::add);

		if (clazz.getSuperclass() != null) {
			findByAnnotation(clazz.getSuperclass(), annotation).forEach(fields::add);
		}

		return fields;
	}

	public static void setValue(Object obj, Field field, Object value)
			throws SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			field.set(obj, value);
		} catch (IllegalAccessException e) {
			try {
				field.setAccessible(true);
				field.set(obj, value);
			} finally {
				field.setAccessible(false);
			}
		}
	}

	public static Object getValue(Object obj, Field field)
			throws SecurityException, IllegalArgumentException, IllegalAccessException {
		try {
			return field.get(obj);
		} catch (IllegalAccessException e) {
			try {
				field.setAccessible(true);
				return field.get(obj);
			} finally {
				field.setAccessible(false);
			}
		}
	}

}
