package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.HashMap;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.Inventory;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.Holder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.InventoryUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodUtilities;

@PandaService(injectable = true, loadOn = LoadStep.LOAD, priority = -997)
public class PandaInventoriesService {

	public static final String INVENTORY_RESOURCE_FILE_TEMPLATE = "%s/Resources/Inventories/%s";
	public static final String INVENTORY_FILE_NAME_TEMPLATE = "inventory_%s.yml";

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private PandaFolder folder;

	private HashMap<Class<? extends ChestInventoryObject>, ChestInventoryData> registeredInventories = new HashMap<>();

	private boolean firstLoad = true;

	@SuppressWarnings("unchecked")
	@LoadMethod
	@ReloadMethod
	private void load() {
		if (firstLoad) {
			filesService.addFolder(folder = new PandaFolder("inventories",
					filesService.getFolder("plugin").getFile().getPath() + "/Inventories"));

			try {
				JavaUtils.getClasses(plugin.getClass().getPackage().getName() + ".Bukkit.Inventories")
						.forEach(clazz -> {
							try {
								if (ChestInventoryObject.class.isAssignableFrom(clazz)
										&& clazz.isAnnotationPresent(Inventory.class)) {
									Inventory inventory = clazz.getAnnotation(Inventory.class);

									if (!inventory.value().isEmpty()) {
										Method yamlSupplierMethod = JavaUtils.getMethods(clazz, true).stream().filter(
												method -> method.isAnnotationPresent(Inventory.YamlSupplier.class))
												.findFirst().orElse(null);

										if (yamlSupplierMethod != null) {
											try {
												Object object = MethodUtilities.executeMethod(clazz,
														yamlSupplierMethod);

												if (object instanceof YamlSection) {
													this.registeredInventories.put(
															(Class<? extends ChestInventoryObject>) clazz,
															ChestInventoryData.initInventory(inventory.value(),
																	(Class<? extends ChestInventoryObject>) clazz,
																	(YamlSection) object));

													plugin.sendInfo(this.getClass(), String.format(
															"Registered new inventory '%s'", inventory.value()));
												} else if (object instanceof PandaYaml) {
													this.registeredInventories.put(
															(Class<? extends ChestInventoryObject>) clazz,
															ChestInventoryData.initInventory(inventory.value(),
																	(Class<? extends ChestInventoryObject>) clazz,
																	(PandaYaml) object));

													plugin.sendInfo(this.getClass(), String.format(
															"Registered new inventory '%s'", inventory.value()));
												} else if (object instanceof InputStream) {
													this.registeredInventories.put(
															(Class<? extends ChestInventoryObject>) clazz,
															ChestInventoryData.initInventory(inventory.value(),
																	(Class<? extends ChestInventoryObject>) clazz,
																	(InputStream) object));

													plugin.sendInfo(this.getClass(), String.format(
															"Registered new inventory '%s'", inventory.value()));
												}
											} catch (Throwable e) {
												plugin.sendError(this.getClass(), String.format(
														"Unable to register inventory '%s'", inventory.value()));
												e.printStackTrace();
											}
										} else {
											try (InputStream resource = plugin.getResource(String.format(
													INVENTORY_RESOURCE_FILE_TEMPLATE,
													plugin.getClass().getPackage().getName().replaceAll("\\.", "/"),
													String.format(INVENTORY_FILE_NAME_TEMPLATE, inventory.value())))) {
												if (resource != null) {
													try {
														this.registeredInventories.put(
																(Class<? extends ChestInventoryObject>) clazz,
																ChestInventoryData.initInventory(inventory.value(),
																		(Class<? extends ChestInventoryObject>) clazz,
																		resource));

														plugin.sendInfo(this.getClass(), String.format(
																"Registered new inventory '%s'", inventory.value()));
													} catch (Throwable e) {
														plugin.sendError(this.getClass(),
																String.format("Unable to register inventory '%s'",
																		inventory.value()));
														e.printStackTrace();
													}
												}
											}
										}
									}
								}
							} catch (Throwable e) {
							}
						});
			} catch (Throwable e) {
				e.printStackTrace();
			}

			firstLoad = false;
		}

		registeredInventories.values().forEach(inventory -> {
			try {
				inventory.load(folder, plugin);
			} catch (Throwable e) {
				plugin.sendError(this.getClass(), String
						.format("Unable to load information from the inventory '%s' properly", inventory.getId()));
				e.printStackTrace();
			}
		});

		InventoryUtilities.forceUpdate(Holder.class);
	}

	public ChestInventoryData parse(String id, Class<? extends ChestInventoryObject> inventoryClass,
			InputStream resource) {
		try {
			ChestInventoryData.initInventory(id, (Class<? extends ChestInventoryObject>) inventoryClass, resource);
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ChestInventoryData parse(String id, Class<? extends ChestInventoryObject> inventoryClass, PandaYaml yaml) {
		try {
			ChestInventoryData.initInventory(id, (Class<? extends ChestInventoryObject>) inventoryClass, yaml);
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public ChestInventoryData findData(Class<? extends ChestInventoryObject> clazz) {
		return this.registeredInventories.get(clazz);
	}

}
