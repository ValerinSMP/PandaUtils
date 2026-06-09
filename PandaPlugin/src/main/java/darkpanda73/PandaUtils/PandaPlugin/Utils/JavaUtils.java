package darkpanda73.PandaUtils.PandaPlugin.Utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import lombok.Getter;

public class JavaUtils {

	@PandaInject
	private static PandaPluginClass plugin;

	private static Reflections reflections;
	private @Getter static Collection<Class<?>> classes;

	@PostInjectMethod
	private static void postInject() {
		reflections = new Reflections(new ConfigurationBuilder()
				.forPackage(plugin.getClass().getPackage().getName(), plugin.getPluginClassLoader())
				.filterInputsBy(new FilterBuilder().includePackage(plugin.getClass().getPackage().getName()))
				.addScanners(Scanners.SubTypes.filterResultsBy(s -> true)));

		classes = reflections.getAll(Scanners.SubTypes).stream()
				.filter(className -> className.startsWith(plugin.getClass().getPackage().getName())).map(className -> {
					try {
						Class<?> clazz = Class.forName(className);
						if (!clazz.isMemberClass() && !clazz.isLocalClass() && !clazz.isAnonymousClass()) {
							return clazz;
						}
					} catch (Throwable e) {
					}
					return null;
				}).filter(Objects::nonNull).collect(Collectors.toList());
	}

	public static List<Field> getFields(Class<?> clazz, boolean staticFields) {
		List<Field> fields = new ArrayList<>();
		if (clazz.getSuperclass() != null) {
			fields.addAll(getFields(clazz.getSuperclass(), staticFields));
		}
		fields.addAll(Arrays.stream(clazz.getDeclaredFields())
				.filter(field -> staticFields == Modifier.isStatic(field.getModifiers())).collect(Collectors.toList()));
		return new LinkedList<>(fields);
	}

	public static void setFieldValue(Object obj, Field field, Object value)
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

	public static Object getFieldValue(Object obj, Field field)
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

	public static List<Method> getMethods(Class<?> clazz, boolean staticMethods) {
		List<Method> methods = new ArrayList<>();
		if (clazz.getSuperclass() != null) {
			methods.addAll(getMethods(clazz.getSuperclass(), staticMethods));
		}
		methods.addAll((Collection<? extends Method>) Arrays.<Method>stream(clazz.getDeclaredMethods())
				.filter(method -> (staticMethods == Modifier.isStatic(method.getModifiers())))
				.collect(Collectors.toList()));
		return new LinkedList<>(methods);
	}

	public static Object executeMethod(Object obj, Method method, Object... params)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		try {
			return method.invoke(obj, params);
		} catch (IllegalAccessException e) {
			try {
				method.setAccessible(true);
				return method.invoke(obj, params);
			} finally {
				method.setAccessible(false);
			}
		}
	}

	public static List<Method> getMethodsIfAnnotationPresent(Class<?> clazz, Class<? extends Annotation> annotation) {
		if (clazz == null) {
			return Collections.emptyList();
		}

		List<Method> list = new LinkedList<>();
		list.addAll(Arrays.stream(clazz.getDeclaredMethods())
				.filter(method -> method.getAnnotationsByType(annotation).length > 0).collect(Collectors.toList()));
		if (clazz.getSuperclass() != null) {
			list.addAll(getMethodsIfAnnotationPresent(clazz.getSuperclass(), annotation));
		}
		if (clazz.getInterfaces().length != 0) {
			for (Class<?> interfaceClass : clazz.getInterfaces()) {
				list.addAll(getMethodsIfAnnotationPresent(interfaceClass, annotation));
			}
		}
		return list;
	}

	public static Constructor<?> getConstructorIfAnnotationPresent(Class<?> service,
			Class<? extends Annotation> annotation) throws NoSuchMethodException, SecurityException {
		if (service == null || Modifier.isAbstract(service.getModifiers())) {
			return null;
		}
		Annotation serviceAnnotation = service.getAnnotation(annotation);
		if (serviceAnnotation != null) {
			return service.getConstructor(new Class[0]);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T extends Annotation> List<T> findAnnotationsByType(Class<?> clazz, Class<T> annotationToSearch) {
		List<T> annotations = new ArrayList<>();
		Arrays.stream(clazz.getAnnotations()).forEach(annotation -> {
			if (annotation.annotationType() == annotationToSearch) {
				annotations.add((T) annotation);
			}
			if (annotation.annotationType().isAnnotationPresent(PandaImport.class)) {
				annotations.addAll(findAnnotationsByType(annotation.annotationType(), annotationToSearch));
			}
		});
		return annotations;
	}

	public static void callMethodIfAnnotationPresent(Class<?> clazz, Object obj,
			Class<? extends Annotation> annotation) {
		getMethods(clazz, (obj == null)).stream().filter(method -> method.getAnnotationsByType(annotation).length > 0)
				.forEach(method -> {
					try {
						executeMethod(obj, method, new Object[0]);
					} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
						e.printStackTrace();
					}
				});
	}

	public static Collection<Class<?>> getClasses(String packageName) {
		return classes.stream().filter(clazz -> clazz.getPackage().getName().startsWith(packageName))
				.collect(Collectors.toList());
	}

}
