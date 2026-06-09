package darkpanda73.PandaUtils.Services.PandaCommandsModule.Utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand.CommandTabCompleter;

public class CommandsUtilities {

	public static Command register(PandaPluginClass plugin, PandaCommand cmd) {
		if (plugin.getCommand(cmd.getCurrentName()) == null) {
			PluginCommand command = getBukkitCommandInstance(cmd.getCurrentName(), plugin);
			command.setExecutor(cmd);
			command.setDescription(cmd.getCurrentDescription());
			command.setAliases(cmd.getCurrentAliases());
			command.setPermission(cmd.getCurrentPermission());
			command.setUsage(cmd.getCurrentUsage());
			command.setTabCompleter(new CommandTabCompleter(cmd));

			try {
				if (!getCommandMap().register(plugin.getDescription().getName(), command)) {
					throw new Exception("Not able to register command " + command.getName());
				}

				plugin.sendInfo(PandaCommandsService.class,
						String.format("Registered a new command with name '%s'", cmd.getCurrentName()));

				return command;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static boolean unregister(PandaPluginClass plugin, Command cmd) {
		try {
			CommandMap commandMap = getCommandMap();
			Map<String, Command> knownCommands = getKnownCommands();
			knownCommands.entrySet().stream().filter(entry -> entry.getValue() == cmd).map(Entry::getKey)
					.collect(Collectors.toList()).forEach(cmdKey -> {
						knownCommands.get(cmdKey).unregister(commandMap);
						knownCommands.remove(cmdKey);
						plugin.sendInfo(PandaCommandsService.class,
								String.format("Unregistered command with name '%s'", cmdKey));
					});
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void syncCommands() {
		try {
			invokeMethod(Bukkit.getServer().getClass(), Bukkit.getServer(), "syncCommands");
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
		}
	}

	private static PluginCommand getBukkitCommandInstance(String name, Plugin plugin) {
		PluginCommand command = null;
		try {
			Constructor<PluginCommand> c = PluginCommand.class
					.getDeclaredConstructor(new Class[] { String.class, Plugin.class });
			c.setAccessible(true);
			command = c.newInstance(new Object[] { name, plugin });
			c.setAccessible(false);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return command;
	}

	private static CommandMap getCommandMap() {
		try {
			return (CommandMap) getField(SimplePluginManager.class, Bukkit.getPluginManager(), "commandMap");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, Command> getKnownCommands() throws Exception {
		CommandMap cm = getCommandMap();

		try {
			return (Map<String, Command>) invokeMethod(cm.getClass(), cm, "getKnownCommands");
		} catch (NoSuchMethodException e) {
			return (Map<String, Command>) getField(cm.getClass(), cm, "knownCommands");
		}
	}

	private static Object getField(Class<?> clazz, Object object, String fieldName)
			throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field field = clazz.getDeclaredField(fieldName);

		Object result;
		try {
			result = field.get(object);
		} catch (IllegalAccessException e) {
			try {
				field.setAccessible(true);
				result = field.get(object);
			} finally {
				field.setAccessible(false);
			}
		}

		return result;
	}

	private static Object invokeMethod(Class<?> clazz, Object object, String methodName, Object... args)
			throws NoSuchMethodException, SecurityException, IllegalAccessException, IllegalArgumentException,
			InvocationTargetException {
		Method method = clazz.getDeclaredMethod(methodName);

		Object result;
		try {
			result = method.invoke(object, args);
		} catch (IllegalAccessException e) {
			try {
				method.setAccessible(true);
				result = method.invoke(object, args);
			} finally {
				method.setAccessible(false);
			}
		}

		return result;
	}
}