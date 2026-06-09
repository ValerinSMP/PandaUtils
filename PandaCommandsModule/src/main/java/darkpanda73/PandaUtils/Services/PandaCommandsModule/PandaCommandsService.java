package darkpanda73.PandaUtils.Services.PandaCommandsModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import org.bukkit.command.Command;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.ShowInfoOn;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Utils.CommandsUtilities;
import darkpanda73.PandaUtils.Services.PandaEventCustomizer.PandaEventCustomizerService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;

@PandaService(loadOn = LoadStep.ENABLE)
public class PandaCommandsService {

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaEventCustomizerService eventCustomizer;

	private HashMap<String, PandaCommand> customCommands = new HashMap<>();
	private HashMap<String, org.bukkit.command.Command> bukkitCommands = new HashMap<>();

	@SuppressWarnings("unchecked")
	@PostInjectMethod
	private void postInject() {
		this.addCommandsFile();

		try {
			List<PandaCommand> showInfoOnList = new ArrayList<>();
			JavaUtils.getClasses().forEach(clazz -> {
				try {
					if (PandaCommand.class.isAssignableFrom(clazz)
							&& clazz.isAnnotationPresent(PandaCommandAnnotation.class)
							&& !clazz.isAnnotationPresent(PandaSubCommandAnnotation.class)) {
						PandaCommand command = ((Class<? extends PandaCommand>) clazz).getConstructor().newInstance();

						if (!command.precondition()) {
							return;
						}

						if (this.customCommands.containsKey(command.getCurrentName().toLowerCase())) {
							throw new InstantiationError("ID is already occupied by another command");
						}

						InjectUtils.injectOnField(command, "plugin", plugin);
						InjectUtils.injectOnField(command, "eventCustomizer", eventCustomizer);
						command.initSubCommands();

						this.customCommands.put(command.getCurrentName().toLowerCase(), command);

						if (clazz.isAnnotationPresent(ShowInfoOn.class)) {
							showInfoOnList.add(command);
						}
					}
				} catch (Throwable e) {
				}
			});
			showInfoOnList.forEach(cmd -> {
				ShowInfoOn showInfoOn = cmd.getClass().getAnnotation(ShowInfoOn.class);
				this.customCommands.values().forEach(customCmd -> {
					if (customCmd.getClass().equals(showInfoOn.value())) {
						customCmd.addRelatedCommand(cmd);
					}
				});
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

		this.customCommands.values().forEach(command -> {
			command.getCustomizableFileFields().forEach((key, value) -> {
				PandaYamlFile yaml = filesService.getYaml(key);
				if (yaml != null) {
					yaml.registerFields(value);
				}
			});
		});
	}

	private void addCommandsFile() {
		PandaFolder pluginFolder = filesService.getFolder("plugin");
		String path = new File(pluginFolder.getFile(), "Commands.yml").getPath();
		InputStream yamlInputStream = plugin.getResource(
				plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Commands.yml");

		PandaYamlFile commandsFile;
		try {
			commandsFile = yamlInputStream != null ? new PandaYamlFile("commands", path, new PandaYaml(yamlInputStream))
					: new PandaYamlFile("commands", path);
		} catch (IOException | YamlException e) {
			plugin.sendError(getClass(), "Unable to use the plugin defailt Commands.yml file:");
			e.printStackTrace();
			commandsFile = new PandaYamlFile("commands", path);
		}

		filesService.addFile(commandsFile);

		PandaYamlFile configFile = filesService.getYaml("config");
		if (configFile != null) {
			try {
				PandaYaml configYaml = new PandaYaml(configFile.getFile());
				PandaYaml commandsYaml = new PandaYaml(commandsFile.getFile());

				YamlSection commandsSection = configYaml.getRoot().getSection("Commands");
				if (commandsSection != null) {
					commandsSection.toMap()
							.forEach((key, value) -> commandsYaml.getRoot().set("Commands." + key, value));
					configYaml.getRoot().remove("Commands");

					configYaml.saveYAML(configFile.getFile());
					commandsYaml.saveYAML(commandsFile.getFile());
				}
			} catch (IOException | YamlException e) {
				plugin.sendError(getClass(),
						"Unable to move command section from Config.yml file to Commands.yml file:");
				e.printStackTrace();
			}
		}
	}

	@LoadMethod
	private void firstLoad() {
		load();

		TasksUtils.executeOnAsync(
				() -> this.customCommands.values().forEach(command -> command.getQueue().clearExpired()));
	}

	private void load() {
		this.customCommands.forEach((key, value) -> {
			if (!this.bukkitCommands.containsKey(key)) {
				Command cmd = CommandsUtilities.register(this.plugin, value);
				if (cmd != null) {
					this.bukkitCommands.put(key, cmd);
				} else {
					plugin.sendError(getClass(),
							String.format("Unable to register command '%s'", value.getCurrentName()));
				}
			}
		});

		TasksUtils.executeWithTimer(
				() -> this.customCommands.values().forEach(PandaCommand::clearCachedAutocompleteLists), 100, 100);
	}

	@ReloadMethod
	private void reload() throws ReportError {
		unload();
		load();

		CommandsUtilities.syncCommands();
	}

	@UnloadMethod
	private void unload() {
		for (org.bukkit.command.Command command : this.bukkitCommands.values()) {
			CommandsUtilities.unregister(this.plugin, command);
		}
		this.bukkitCommands.clear();
	}

	@SuppressWarnings("unchecked")
	public <T extends PandaCommand> T getCommandByName(String name) {
		return (T) this.customCommands.get(name.toLowerCase());
	}

	@SuppressWarnings("unchecked")
	public <T extends PandaCommand> T getCommandByClass(Class<T> clazz) {
		return (T) this.customCommands.values().stream().filter(cmd -> cmd.getClass() == clazz).findFirst()
				.orElse(null);
	}

	public <T extends PandaSubCommand> T getSubCommandByClass(Class<T> clazz) {
		return (T) this.customCommands.values().stream().map(cmd -> getSubCommandByClass(clazz, cmd))
				.filter(Objects::nonNull).findFirst().orElse(null);
	}

	@SuppressWarnings("unchecked")
	private <T extends PandaSubCommand> T getSubCommandByClass(Class<T> clazz, PandaSubCommand currentSubCommand) {
		if (currentSubCommand.getClass() == clazz) {
			return (T) currentSubCommand;
		} else if (currentSubCommand instanceof PandaCommand) {
			return ((PandaCommand) currentSubCommand).getCommands().stream()
					.map(cmd -> getSubCommandByClass(clazz, cmd)).filter(Objects::nonNull).findFirst().orElse(null);
		}
		return null;
	}

	public boolean registerCommand(PandaCommand command) {
		String name = command.getCurrentName().toLowerCase();
		if (!this.bukkitCommands.containsKey(name)) {
			InjectUtils.injectOnField(command, "plugin", plugin);
			command.initSubCommands();

			Command cmd = CommandsUtilities.register(this.plugin, command);

			if (cmd != null) {
				this.bukkitCommands.put(name, cmd);
				this.customCommands.put(name, command);

				CommandsUtilities.syncCommands();

				return true;
			} else {
				plugin.sendError(getClass(),
						String.format("Unable to register command '%s'", command.getCurrentName()));
			}
			return false;
		}
		return true;
	}

	public boolean unregisterCommand(PandaCommand command) {
		String name = command.getCurrentName().toLowerCase();
		if (this.bukkitCommands.containsKey(name)) {
			Command cmd = this.bukkitCommands.get(name);
			if (cmd != null) {
				CommandsUtilities.unregister(plugin, cmd);
				this.bukkitCommands.remove(name);
				this.customCommands.remove(name);

				CommandsUtilities.syncCommands();

				return true;
			}
			return false;
		}
		return true;
	}

}
