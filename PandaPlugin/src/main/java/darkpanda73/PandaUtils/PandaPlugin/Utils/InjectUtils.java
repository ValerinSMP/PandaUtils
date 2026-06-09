package darkpanda73.PandaUtils.PandaPlugin.Utils;

import java.util.HashMap;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;

public class InjectUtils {

	public static void inject(Object obj, HashMap<Class<?>, Object> injectables) {
		try {
			JavaUtils.getFields(obj.getClass(), false).stream()
					.filter(field -> field.isAnnotationPresent(PandaInject.class)).forEach(field -> {
						Class<?> injectable = injectables.keySet().stream()
								.filter(injectClass -> field.getType().isAssignableFrom(injectClass)).findFirst()
								.orElse(null);
						if (injectable != null) {
							try {
								JavaUtils.setFieldValue(obj, field, injectables.get(injectable));
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(obj.getClass(), obj, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

	public static void inject(Class<?> clazz, HashMap<Class<?>, Object> injectables) {
		try {
			JavaUtils.getFields(clazz, true).stream().filter(field -> field.isAnnotationPresent(PandaInject.class))
					.forEach(field -> {
						Class<?> injectable = injectables.keySet().stream()
								.filter(injectClass -> field.getType().isAssignableFrom(injectClass)).findFirst()
								.orElse(null);
						if (injectable != null) {
							try {
								JavaUtils.setFieldValue(null, field, injectables.get(injectable));
							} catch (NoClassDefFoundError e) {
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(clazz, null, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

	public static void inject(Object object, Object injectable) {
		try {
			JavaUtils.getFields(object.getClass(), false).stream()
					.filter(field -> field.isAnnotationPresent(PandaInject.class)).forEach(field -> {
						if (field.getType().isAssignableFrom(injectable.getClass())) {
							try {
								JavaUtils.setFieldValue(object, field, injectable);
							} catch (NoClassDefFoundError e) {
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(object.getClass(), object, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

	public static void inject(Class<?> clazz, Object injectable) {
		try {
			JavaUtils.getFields(clazz, true).stream().filter(field -> field.isAnnotationPresent(PandaInject.class))
					.forEach(field -> {

						if (field.getType().isAssignableFrom(injectable.getClass())) {
							try {
								JavaUtils.setFieldValue(null, field, injectable);
							} catch (NoClassDefFoundError e) {
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(clazz, null, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

	public static void injectOnField(Object object, String fieldName, Object injectable) {
		try {
			JavaUtils.getFields(object.getClass(), false).stream()
					.filter(field -> field.isAnnotationPresent(PandaInject.class)).forEach(field -> {
						if (field.getName().equals(fieldName)) {
							try {
								JavaUtils.setFieldValue(object, field, injectable);
							} catch (NoClassDefFoundError e) {
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(object.getClass(), object, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

	public static void injectOnField(Class<?> clazz, String fieldName, Object injectable) {
		try {
			JavaUtils.getFields(clazz, true).stream().filter(field -> field.isAnnotationPresent(PandaInject.class))
					.forEach(field -> {
						if (field.getName().equals(fieldName)) {
							try {
								JavaUtils.setFieldValue(null, field, injectable);
							} catch (NoClassDefFoundError e) {
							} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
								e.printStackTrace();
							}
						}
					});
			JavaUtils.callMethodIfAnnotationPresent(clazz, null, PostInjectMethod.class);
		} catch (NoClassDefFoundError e) {
		}
	}

}
