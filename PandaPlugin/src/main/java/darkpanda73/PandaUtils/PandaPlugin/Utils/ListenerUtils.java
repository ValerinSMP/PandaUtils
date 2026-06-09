package darkpanda73.PandaUtils.PandaPlugin.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.bukkit.event.Listener;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;

public class ListenerUtils {

	@SuppressWarnings("unchecked")
	public static List<Constructor<? extends Listener>> getListenerConstructors(PandaPluginClass plugin,
			ClassLoader pluginClassLoader) throws Exception {
		List<Constructor<? extends Listener>> constructors = new ArrayList<>();

		JavaUtils.getClasses().stream().map(clazz -> {
			try {
				return clazz != null && Listener.class.isAssignableFrom(clazz)
						? JavaUtils.getConstructorIfAnnotationPresent(clazz, PandaListener.class)
						: null;
			} catch (NoSuchMethodException e) {
				plugin.sendError(null, String.format(
						"Class with name '%s' does not have a an empty constructor. Listeners must have an empty constructor to be instantiated.",
						clazz.getSimpleName()));
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::nonNull)
				.forEach(constructor -> constructors.add((Constructor<? extends Listener>) constructor));

		JavaUtils.findAnnotationsByType(plugin.getClass(), PandaListeners.class).forEach(annotation -> {
			Arrays.stream(annotation.value()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
					.forEach(clazz -> {
						try {
							Constructor<?> constructor = JavaUtils.getConstructorIfAnnotationPresent(clazz,
									PandaListener.class);
							if (constructor != null) {
								constructors.add((Constructor<? extends Listener>) constructor);
							}
						} catch (NoSuchMethodException e) {
							plugin.sendError(null, String.format(
									"Class with name '%s' does not have a an empty constructor. Listeners must have an empty constructor to be instantiated.",
									clazz.getSimpleName()));
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					});
		});

		return constructors;
	}
}
