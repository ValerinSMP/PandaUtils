package darkpanda73.PandaUtils.Services.PandaPermissionsModule;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaCustomizablePermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaParametizedPermission;
import darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects.PandaPermission;

@PandaService(loadOn = LoadStep.LOAD, priority = -997)
public abstract class PandaPermissionsService {

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private List<PandaPermission> permissions = new ArrayList<>();
	private List<Permission> bukkitPermissions = new ArrayList<>();

	@PostInjectMethod
	private void postInject() {
		addCommandsFile();

		BiConsumer<Field, Object> consumer = (field, object) -> {
			if (PandaPermission.class.isAssignableFrom(field.getType())) {
				try {
					PandaPermission pandaPermission = (PandaPermission) JavaUtils.getFieldValue(object, field);

					permissions.add(pandaPermission);

					if (pandaPermission instanceof PandaCustomizablePermission) {
						PandaYamlFile config = filesService.getYaml("permissions");
						if (config != null) {
							config.registerFields(((PandaCustomizablePermission) pandaPermission).getFileField());
						}
					}
				} catch (IllegalArgumentException | IllegalAccessException e) {
					e.printStackTrace();
				}
			}
		};
		JavaUtils.getFields(getClass(), true).forEach(field -> consumer.accept(field, null));
		JavaUtils.getFields(getClass(), false).forEach(field -> consumer.accept(field, this));
	}

	private void addCommandsFile() {
		PandaFolder pluginFolder = filesService.getFolder("plugin");
		String path = new File(pluginFolder.getFile(), "Permissions.yml").getPath();
		InputStream yamlInputStream = plugin.getResource(
				plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Permissions.yml");

		PandaYamlFile permissionsFile;
		try {
			permissionsFile = yamlInputStream != null
					? new PandaYamlFile("permissions", path, new PandaYaml(yamlInputStream))
					: new PandaYamlFile("permissions", path);
		} catch (IOException | YamlException e) {
			plugin.sendError(getClass(), "Unable to use the plugin defailt Permissions.yml file:");
			e.printStackTrace();
			permissionsFile = new PandaYamlFile("permissions", path);
		}

		filesService.addFile(permissionsFile);

		PandaYamlFile configFile = filesService.getYaml("config");
		if (configFile != null) {
			try {
				PandaYaml configYaml = new PandaYaml(configFile.getFile());
				PandaYaml permissionsYaml = new PandaYaml(permissionsFile.getFile());

				YamlSection permissionsSection = configYaml.getRoot().getSection("Permissions");
				if (permissionsSection != null) {
					permissionsSection.toMap()
							.forEach((key, value) -> permissionsYaml.getRoot().set("Permissions." + key, value));
					configYaml.getRoot().remove("Permissions");

					configYaml.saveYAML(configFile.getFile());
					permissionsYaml.saveYAML(permissionsFile.getFile());
				}
			} catch (IOException | YamlException e) {
				plugin.sendError(getClass(),
						"Unable to move command section from Config.yml file to Permissions.yml file:");
				e.printStackTrace();
			}
		}
	}

	@LoadMethod
	private void load() {
		permissions.forEach(pandaPermission -> {
			if (pandaPermission instanceof PandaParametizedPermission) {
				return;
			}

			String permission = pandaPermission.getPermission();

			if (permission == null || permission.isEmpty()) {
				return;
			}

			Permission bukkitPermission;

			switch (pandaPermission.getInsertion()) {
			case ALL:
				bukkitPermission = new Permission(permission, PermissionDefault.TRUE);
				break;
			case OP:
				bukkitPermission = new Permission(permission, PermissionDefault.OP);
				break;
			default:
				return;
			}

			Bukkit.getPluginManager().addPermission(bukkitPermission);
			bukkitPermissions.add(bukkitPermission);
		});
	}

	@UnloadMethod
	private void unload() {
		bukkitPermissions.forEach(Bukkit.getPluginManager()::removePermission);
		bukkitPermissions.clear();
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

}
