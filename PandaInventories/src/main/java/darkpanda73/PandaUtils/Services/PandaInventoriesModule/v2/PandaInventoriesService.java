package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Annotations.RegisteredPandaInventoryCustomizer;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Annotations.RegisteredPandaInventorySettings;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaDefaultInventorySettings;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaInventoryCommand;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaInventoryCustomizer;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaInventorySettings;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Utils.InventoryUtilities;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Utils.InventoryUtilities.GeneratedInventory;

@PandaService(injectable = true, loadOn = LoadStep.LOAD, priority = -997)
public class PandaInventoriesService {

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaCommandsService commandsService;

	@PandaInject
	private PandaPluginClass plugin;

	private Map<String, PandaDefaultInventorySettings> registeredInventories = new HashMap<>();
	private Map<String, PandaInventoryCustomizer> registeredCustomizers = new HashMap<>();

	private Map<String, PandaInventorySettings> loadedInventories = new HashMap<>();

	private PandaFolder inventoriesFolder;

	@PostInjectMethod
	private void postInject() {
		filesService.addFolder(inventoriesFolder = new PandaFolder("inventories",
				filesService.getFolder("plugin").getFile().getPath() + "/Inventories"));
	}

	@SuppressWarnings("unchecked")
	@LoadMethod
	private void load() {
		try {
			JavaUtils.getClasses(plugin.getClass().getPackage().getName()).forEach(clazz -> {
				try {
					if (PandaDefaultInventorySettings.class.isAssignableFrom(clazz)
							&& clazz.isAnnotationPresent(RegisteredPandaInventorySettings.class)) {
						this.registeredInventories.put(
								clazz.getAnnotation(RegisteredPandaInventorySettings.class).value().toLowerCase(),
								((Class<PandaDefaultInventorySettings>) clazz).getConstructor().newInstance());
					} else if (PandaInventoryCustomizer.class.isAssignableFrom(clazz)
							&& clazz.isAnnotationPresent(RegisteredPandaInventoryCustomizer.class)) {
						this.registeredCustomizers.put(
								clazz.getAnnotation(RegisteredPandaInventoryCustomizer.class).value().toLowerCase(),
								((Class<PandaInventoryCustomizer>) clazz).getConstructor().newInstance());
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

		reload();
	}

	@ReloadMethod
	private void reload() {
		this.loadedInventories.values().forEach(inventory -> commandsService.unregisterCommand(inventory.getCommand()));
		this.loadedInventories.clear();

		getFiles(this.inventoriesFolder.getFile()).forEach(file -> {
			try {
				GeneratedInventory inventory = InventoryUtilities.fileToInventory(file);

				if (commandsService != null && inventory.getCommand() != null) {
					try {
						PandaCommand command = new PandaInventoryCommand(inventory.getCommand().getName(),
								inventory.getCommand().getDescription(), inventory.getCommand().getAliases(),
								inventory.getInventory(),
								this.registeredCustomizers.get(inventory.getInventory().getId().toLowerCase()));

						if (commandsService.registerCommand(command)) {
							inventory.getInventory().setCommand(command);
						} else {
							PandaPluginClass.getSimpleLogger().sendWarning(String.format(
									"Unable to register command for inventory '%s'. Check the inventory's configuration.",
									inventory.getInventory().getId()));
						}
					} catch (InstantiationException e) {
						e.printStackTrace();
					}
				}

				this.loadedInventories.put(inventory.getInventory().getId().toLowerCase(), inventory.getInventory());
			} catch (YamlException | IOException e) {
				PandaPluginClass.getSimpleLogger().sendWarning(
						String.format("Unable to load inventory from file '%s': %s", file.getName(), e.getMessage()));
				e.printStackTrace();
			}
		});
	}

	public void registerInventory(String id, PandaDefaultInventorySettings inventory) {
		this.registeredInventories.put(id, inventory);
	}

	public List<File> getFiles(File folder) {
		return getFiles(folder, new ArrayList<>());
	}

	public List<File> getFiles(File folder, List<File> currentFiles) {
		for (File file : folder.listFiles()) {
			if (file.isFile()) {
				currentFiles.add(file);
			} else {
				getFiles(file, currentFiles);
			}
		}

		return currentFiles;
	}

//
//	@PandaInject
//	private PandaPluginClass plugin;
//
//	@PandaInject
//	private PandaFilesService filesService;
//
//	private PandaFolder folder;
//
//	private HashMap<Class<? extends ChestInventoryObject>, ChestInventoryData> registeredInventories = new HashMap<>();
//
//	private boolean firstLoad = true;
//
//	@SuppressWarnings("unchecked")
//	@LoadMethod
//	@ReloadMethod
//	private void load() {
//		if (firstLoad) {
//			filesService.addFolder(folder = new PandaFolder("inventories",
//					filesService.getFolder("plugin").getFile().getPath() + "/Inventories"));
//
//			try {
//				JavaUtils.getClasses(plugin.getClass().getPackage().getName() + ".Bukkit.Inventories")
//						.forEach(clazz -> {
//							try {
//								if (ChestInventoryObject.class.isAssignableFrom(clazz)
//										&& clazz.isAnnotationPresent(Inventory.class)) {
//									Inventory inventory = clazz.getAnnotation(Inventory.class);
//
//									if (!inventory.value().isEmpty()) {
//										Method yamlSupplierMethod = JavaUtils.getMethods(clazz, true).stream().filter(
//												method -> method.isAnnotationPresent(Inventory.YamlSupplier.class))
//												.findFirst().orElse(null);
//
//										if (yamlSupplierMethod != null) {
//											try {
//												Object object = MethodUtilities.executeMethod(clazz,
//														yamlSupplierMethod);
//
//												if (object instanceof YamlSection) {
//													this.registeredInventories.put(
//															(Class<? extends ChestInventoryObject>) clazz,
//															ChestInventoryData.initInventory(inventory.value(),
//																	(Class<? extends ChestInventoryObject>) clazz,
//																	(YamlSection) object));
//
//													plugin.sendInfo(this.getClass(), String.format(
//															"Registered new inventory '%s'", inventory.value()));
//												} else if (object instanceof PandaYaml) {
//													this.registeredInventories.put(
//															(Class<? extends ChestInventoryObject>) clazz,
//															ChestInventoryData.initInventory(inventory.value(),
//																	(Class<? extends ChestInventoryObject>) clazz,
//																	(PandaYaml) object));
//
//													plugin.sendInfo(this.getClass(), String.format(
//															"Registered new inventory '%s'", inventory.value()));
//												} else if (object instanceof InputStream) {
//													this.registeredInventories.put(
//															(Class<? extends ChestInventoryObject>) clazz,
//															ChestInventoryData.initInventory(inventory.value(),
//																	(Class<? extends ChestInventoryObject>) clazz,
//																	(InputStream) object));
//
//													plugin.sendInfo(this.getClass(), String.format(
//															"Registered new inventory '%s'", inventory.value()));
//												}
//											} catch (Throwable e) {
//												plugin.sendError(this.getClass(), String.format(
//														"Unable to register inventory '%s'", inventory.value()));
//												e.printStackTrace();
//											}
//										} else {
//											try (InputStream resource = plugin.getResource(String.format(
//													INVENTORY_RESOURCE_FILE_TEMPLATE,
//													plugin.getClass().getPackage().getName().replaceAll("\\.", "/"),
//													String.format(INVENTORY_FILE_NAME_TEMPLATE, inventory.value())))) {
//												if (resource != null) {
//													try {
//														this.registeredInventories.put(
//																(Class<? extends ChestInventoryObject>) clazz,
//																ChestInventoryData.initInventory(inventory.value(),
//																		(Class<? extends ChestInventoryObject>) clazz,
//																		resource));
//
//														plugin.sendInfo(this.getClass(), String.format(
//																"Registered new inventory '%s'", inventory.value()));
//													} catch (Throwable e) {
//														plugin.sendError(this.getClass(),
//																String.format("Unable to register inventory '%s'",
//																		inventory.value()));
//														e.printStackTrace();
//													}
//												}
//											}
//										}
//									}
//								}
//							} catch (Throwable e) {
//							}
//						});
//			} catch (Throwable e) {
//				e.printStackTrace();
//			}
//
//			firstLoad = false;
//		}
//
//		registeredInventories.values().forEach(inventory -> {
//			try {
//				inventory.load(folder, plugin);
//			} catch (Throwable e) {
//				plugin.sendError(this.getClass(), String
//						.format("Unable to load information from the inventory '%s' properly", inventory.getId()));
//				e.printStackTrace();
//			}
//		});
//
//		InventoryUtilities.forceUpdate(Holder.class);
//	}
//
//	public ChestInventoryData parse(String id, Class<? extends ChestInventoryObject> inventoryClass,
//			InputStream resource) {
//		try {
//			ChestInventoryData.initInventory(id, (Class<? extends ChestInventoryObject>) inventoryClass, resource);
//		} catch (YamlException | IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public ChestInventoryData parse(String id, Class<? extends ChestInventoryObject> inventoryClass, PandaYaml yaml) {
//		try {
//			ChestInventoryData.initInventory(id, (Class<? extends ChestInventoryObject>) inventoryClass, yaml);
//		} catch (YamlException | IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//
//	public ChestInventoryData findData(Class<? extends ChestInventoryObject> clazz) {
//		return this.registeredInventories.get(clazz);
//	}

}
