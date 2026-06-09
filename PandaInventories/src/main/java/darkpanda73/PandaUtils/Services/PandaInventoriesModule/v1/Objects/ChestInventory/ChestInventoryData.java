package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlNamedObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemPregenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ChestInventoryData {

	private static final String VERSION_PATH = "Version";
	private static final String TITLE_PATH = "Title";
	private static final String SIZE_PATH = "Size";
	private static final String ITEMS_PATH = "Items";
	private static final String OLD_ITEMS_PATH = "Old-items";
	private static final String ITEM_VERSION_PATH = "Version";

	private @Getter @NonNull String id;
	private @NonNull Class<? extends ChestInventoryObject> inventoryObjectClass;
	private File file;

	private @Getter HashMap<String, Object> defaultFields = new LinkedHashMap<>();
	private @Getter List<DefaultItem> defaultItems = new LinkedList<>();

	private @Getter String title;
	private @Getter int size;
	private @Getter HashMap<String, Object> customFields = new LinkedHashMap<>();
	private @Getter List<Item> items = new LinkedList<>();

	public void load(PandaFolder inventoriesFolder, PandaPluginClass plugin) {
		try {
			if (file == null) {
				this.file = new File(inventoriesFolder.getFile(),
						String.format(PandaInventoriesService.INVENTORY_FILE_NAME_TEMPLATE, this.id));
			}

			final PandaYaml yaml = new PandaYaml(file);

			try {
				if (!yaml.getRoot().has(VERSION_PATH)) {
					yaml.getRoot().set(VERSION_PATH, 0);
				}

				defaultFields.forEach((field, value) -> {
					if (!yaml.getRoot().has(field)) {
						yaml.getRoot().set(field, value);
					}
				});

				AtomicInteger version = new AtomicInteger(yaml.getRoot().getData(VERSION_PATH).getInteger());
				AtomicInteger newVersion = new AtomicInteger(version.get());

				this.getDefaultItems().stream().filter(itemData -> itemData.getVersion() > version.get())
						.forEach(itemData -> {
							String path = String.format("%s.%s", ITEMS_PATH, itemData.getId());
							if (yaml.getRoot().has(path)) {
								YamlSection oldItem = yaml.getRoot().getSection(path);
								oldItem.getAllNamedChilds().forEach(
										yamlData -> yaml.getRoot().set(String.format("%s.%s.%s", OLD_ITEMS_PATH,
												itemData.getId(), yamlData.toPath(oldItem)), yamlData.getData()));
								yaml.getRoot().remove(path);
							}

							itemData.getData().forEach((key, value) -> yaml.getRoot()
									.set(String.format("%s.%s.%s", ITEMS_PATH, itemData.getId(), key), value));

							if (itemData.getVersion() > version.get()) {
								newVersion.set(itemData.getVersion());
							}
						});

				yaml.getRoot().set(VERSION_PATH, newVersion.get());

				yaml.saveYAML(file);
			} catch (IOException e) {
				e.printStackTrace();
				yaml.loadYAML(file);
			}

			yaml.getRoot().getNamedChilds().forEach(child -> {
				if (child.getName().equals(ITEMS_PATH)) {
					return;
				}

				registeringField(plugin, child);
			});

			YamlSection itemsSection = yaml.getRoot().getSection(ITEMS_PATH);
			if (itemsSection != null) {
				this.items = new ArrayList<>();

				itemsSection.getNamedChilds().stream().filter(YamlNamedObject::isYamlSection)
						.map(YamlNamedObject::asYamlSection).forEach(section -> registeringItem(plugin, section));

				JavaUtils.getMethodsIfAnnotationPresent(inventoryObjectClass, ItemPregenerator.class)
						.forEach(method -> {
							for (ItemPregenerator annotation : method.getAnnotationsByType(ItemPregenerator.class)) {
								String itemId = annotation.value();
								Optional<Item> foundItem = this.items.stream()
										.filter(item -> item.getId().equals(itemId)).findFirst();
								if (foundItem.isPresent()) {
									foundItem.get().setPregenerateItemMethod(method);
								}
							}
						});

				JavaUtils.getMethodsIfAnnotationPresent(inventoryObjectClass, ItemGenerator.class).forEach(method -> {
					for (ItemGenerator annotation : method.getAnnotationsByType(ItemGenerator.class)) {
						String itemId = annotation.value();
						Optional<Item> foundItem = this.items.stream().filter(item -> item.getId().equals(itemId))
								.findFirst();
						if (foundItem.isPresent()) {
							foundItem.get().setGenerateItemMethod(method);
						}
					}
				});

				JavaUtils.getMethodsIfAnnotationPresent(inventoryObjectClass, ItemExecutor.class).forEach(method -> {
					for (ItemExecutor annotation : method.getAnnotationsByType(ItemExecutor.class)) {
						String itemId = annotation.value();
						Optional<Item> foundItem = this.items.stream().filter(item -> item.getId().equals(itemId))
								.findFirst();
						if (foundItem.isPresent()) {
							foundItem.get().setOnClickItemMethod(method);
						}
					}
				});

				this.items.forEach(Item::loadItemData);
			}
		} catch (YamlException | IOException e) {
			e.printStackTrace();
		}
	}

	protected void registeringField(PandaPluginClass plugin, YamlNamedObject field) {
		if (field.getName().equals(TITLE_PATH)) {
			this.title = field.asYamlData().getString();
		} else if (field.getName().equals(SIZE_PATH)) {
			this.size = field.asYamlData().getInteger();
		} else {
			if (field.isYamlSection()) {
				field.asYamlSection().getAllNamedChilds()
						.forEach(child -> customFields.put(child.toPath(), child.getData()));
			} else if (field.isYamlData()) {
				customFields.put(field.getName(), field.asYamlData().getData());
			}
		}
	}

	protected void registeringItem(PandaPluginClass plugin, YamlSection itemSection) {
		try {
			items.add(new Item(itemSection.getName(), itemSection.toMap()));
		} catch (Exception e) {
			plugin.sendError(this.getClass(),
					String.format("Unable to process item '%s' properly:", itemSection.getName()));
			e.printStackTrace();
		}

	}

	public static ChestInventoryData initInventory(String id,
			Class<? extends ChestInventoryObject> inventoryObjectClass, InputStream stream)
			throws YamlException, IOException {
		return initInventory(id, inventoryObjectClass, new PandaYaml(stream));
	}

	public static ChestInventoryData initInventory(String id,
			Class<? extends ChestInventoryObject> inventoryObjectClass, PandaYaml yaml)
			throws YamlException, IOException {
		return initInventory(id, inventoryObjectClass, yaml.getRoot());
	}

	public static ChestInventoryData initInventory(String id,
			Class<? extends ChestInventoryObject> inventoryObjectClass, YamlSection yamlSection)
			throws YamlException, IOException {
		final ChestInventoryData data;
		if (PagedChestInventoryObject.class.isAssignableFrom(inventoryObjectClass)) {
			data = new PagedChestInventoryData(id, inventoryObjectClass);
		} else {
			data = new ChestInventoryData(id, inventoryObjectClass);
		}

		yamlSection.getNamedChilds().forEach(child -> {
			if (child.getName().equals(ITEMS_PATH) && child.isYamlSection()) {
				child.asYamlSection().getNamedChilds().forEach(itemChild -> {
					final DefaultItem defaultItem = new DefaultItem(itemChild.getName());
					itemChild.asYamlSection().toMap().forEach((itemSectionKey, itemSectionValue) -> {
						if (itemSectionKey.equals(ITEM_VERSION_PATH)) {
							defaultItem.setVersion(((Long) itemSectionValue).intValue());
						} else {
							defaultItem.getData().put(itemSectionKey, itemSectionValue);
						}
					});
					data.defaultItems.add(defaultItem);
				});
			} else if (child.isYamlData()) {
				data.getDefaultFields().put(child.getName(), child.asYamlData().getData());
			} else if (child.isYamlSection()) {
				child.asYamlSection().toMap().forEach((key, value) -> data.getDefaultFields()
						.put(String.format("%s.%s", child.getName(), key), value));
			}
		});

		return data;
	}

}
