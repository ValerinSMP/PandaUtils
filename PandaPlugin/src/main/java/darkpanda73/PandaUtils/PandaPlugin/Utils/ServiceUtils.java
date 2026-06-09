package darkpanda73.PandaUtils.PandaPlugin.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;

public class ServiceUtils {

	public static List<Constructor<?>> getServiceConstructors(PandaPluginClass plugin, ClassLoader pluginClassLoader)
			throws Exception {
		List<Constructor<?>> constructors = new ArrayList<>();

		JavaUtils.getClasses().stream().map(service -> {
			try {
				return service != null ? JavaUtils.getConstructorIfAnnotationPresent(service, PandaService.class)
						: null;
			} catch (NoSuchMethodException e) {
				plugin.sendError(null, String.format(
						"Class with name '%s' does not have a an empty constructor. Services must have an empty constructor to be instantiated.",
						service.getSimpleName()));
			} catch (SecurityException e) {
				e.printStackTrace();
			}
			return null;
		}).filter(Objects::nonNull).forEach(constructors::add);

		JavaUtils.findAnnotationsByType(plugin.getClass(), PandaServices.class).forEach(annotation -> {
			Arrays.stream(annotation.value()).filter(service -> !Modifier.isAbstract(service.getModifiers()))
					.forEach(service -> {
						try {
							Constructor<?> constructor = JavaUtils.getConstructorIfAnnotationPresent(service,
									PandaService.class);
							if (constructor != null) {
								constructors.add(constructor);
							}
						} catch (NoSuchMethodException e) {
							plugin.sendError(null, String.format(
									"Class with name '%s' does not have a an empty constructor. Services must have an empty constructor to be instantiated.",
									service.getSimpleName()));
						} catch (SecurityException e) {
							e.printStackTrace();
						}
					});
		});

		return constructors;
	}

	public static boolean loadService(PandaPluginClass plugin, Object service) {
		try {
			long start = System.currentTimeMillis();

			Method loadMethod = JavaUtils.getMethods(service.getClass(), false).stream()
					.filter(method -> method.isAnnotationPresent(PandaService.LoadMethod.class)).findFirst()
					.orElse(null);

			if (loadMethod != null) {
				ReportResult result = ReportUtils.generateResult(service, loadMethod);

				if (result.isSuccess()) {
					plugin.sendInfo(null, String.format("Loaded service '%s' successfully. (%d ms)",
							service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					return true;
				} else if (!result.isCritical()) {
					plugin.sendWarning(null,
							String.format("Loaded service '%s' with errors. No critical errors found. (%d ms)",
									service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					result.getErrors().forEach(error -> {
						plugin.sendWarning(null, error.getMessage());
						if (error.getCause() != null) {
							error.getCause().printStackTrace();
						}
					});
					return true;
				} else {
					plugin.sendError(null, String.format("Unable to load service '%s'. (%d ms)",
							service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					result.getErrors().forEach(error -> {
						plugin.sendError(null, error.getMessage());
						if (error.getCause() != null) {
							error.getCause().printStackTrace();
						}
					});
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			plugin.sendError(null,
					String.format("Error trying to load service '%s':", service.getClass().getSimpleName()));
			e.printStackTrace();
			return false;
		}
	}

	public static boolean reloadService(PandaPluginClass plugin, Object service) {
		try {
			long start = System.currentTimeMillis();

			Method reloadMethod = JavaUtils.getMethods(service.getClass(), false).stream()
					.filter(method -> method.isAnnotationPresent(PandaService.ReloadMethod.class)).findFirst()
					.orElse(null);

			if (reloadMethod != null) {
				ReportResult result = ReportUtils.generateResult(service, reloadMethod);

				if (result.isSuccess()) {
					plugin.sendInfo(null, String.format("Reloaded service '%s' successfully. (%d ms)",
							service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					return true;
				} else if (!result.isCritical()) {
					plugin.sendWarning(null,
							String.format("Reloaded service '%s' with errors. No critical errors found. (%d ms)",
									service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					result.getErrors().forEach(error -> {
						plugin.sendWarning(null, error.getMessage());
						if (error.getCause() != null) {
							error.getCause().printStackTrace();
						}
					});
					return true;
				} else {
					plugin.sendError(null, String.format("Unable to reload service '%s'. (%d ms)",
							service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					result.getErrors().forEach(error -> {
						plugin.sendError(null, error.getMessage());
						if (error.getCause() != null) {
							error.getCause().printStackTrace();
						}
					});
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			plugin.sendError(null,
					String.format("Error trying to reload service '%s':", service.getClass().getSimpleName()));
			e.printStackTrace();
			return false;
		}
	}

	public static boolean unloadService(PandaPluginClass plugin, Object service) {
		try {
			long start = System.currentTimeMillis();

			Method unloadMethod = JavaUtils.getMethods(service.getClass(), false).stream()
					.filter(method -> method.isAnnotationPresent(PandaService.UnloadMethod.class)).findFirst()
					.orElse(null);

			if (unloadMethod != null) {
				ReportResult result = ReportUtils.generateResult(service, unloadMethod);

				if (result.isSuccess()) {
					plugin.sendInfo(null, String.format("Unloaded service '%s' successfully. (%d ms)",
							service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					return true;
				} else if (!result.isCritical()) {
					plugin.sendWarning(null,
							String.format("Unloaded service '%s' with errors. No critical errors found. (%d ms)",
									service.getClass().getSimpleName(), (System.currentTimeMillis() - start)));
					result.getErrors().forEach(error -> {
						plugin.sendWarning(null, error.getMessage());
						if (error.getCause() != null) {
							error.getCause().printStackTrace();
						}
					});
					return true;
				} else {
					return false;
				}
			} else {
				return true;
			}
		} catch (Exception e) {
			plugin.sendError(null,
					String.format("Error trying to unload service '%s':", service.getClass().getSimpleName()));
			e.printStackTrace();
			return false;
		}
	}

}
