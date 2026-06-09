package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlNamedObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.ItemUtilities;
import lombok.Getter;
import lombok.NonNull;

public class PagedChestInventoryData extends ChestInventoryData {

	public static final String ENTITY_PATH = "Entity";
	public static final String ENTITY_NAME_PATH = "Entity.Name";
	public static final String ENTITY_LORE_PATH = "Entity.Lore";
	public static final String ENTITY_SLOTS_PATH = "Entity.Slots";

	private @Getter String entityName = null;
	private @Getter List<String> entityLore = Collections.emptyList();
	private @Getter int[] entitySlots = new int[0];

	public PagedChestInventoryData(@NonNull String id,
			@NonNull Class<? extends ChestInventoryObject> inventoryObjectClass) {
		super(id, inventoryObjectClass);
	}

	@Override
	protected void registeringField(PandaPluginClass plugin, YamlNamedObject field) {
		if (field.getName().equals(ENTITY_PATH)) {
			field.asYamlSection().getNamedChilds().forEach(entityField -> {
				if (entityField.toPath().equals(ENTITY_SLOTS_PATH)) {
					String slotsTemplate = entityField.asYamlData().getString();

					if (slotsTemplate != null && !slotsTemplate.equals("-1")) {
						String[] split = slotsTemplate.split(";");
						List<Integer> slots = new ArrayList<>();
						for (String slot : split) {
							if (slot.matches(ItemUtilities.SLOT_SINGLE_REGEX)) {
								slots.add(Integer.parseInt(slot));
							} else if (slot.matches(ItemUtilities.SLOT_RANGE_REGEX)) {
								String[] slotSplit = slot.split("-");
								int max = Integer.parseInt(slotSplit[1]);
								for (int i = Integer.parseInt(slotSplit[0]); i <= max; i++) {
									slots.add(i);
								}
							}
						}

						this.entitySlots = slots.stream().mapToInt(i -> i).toArray();
					} else {
						this.entitySlots = new int[0];
					}
				} else if (entityField.toPath().equals(ENTITY_NAME_PATH)) {
					this.entityName = entityField.asYamlData().getString();
				} else if (entityField.toPath().equals(ENTITY_LORE_PATH)) {
					this.entityLore = entityField.asYamlData().getStringList(Collections.emptyList());
				}
			});
		}
		super.registeringField(plugin, field);
	}

	@Override
	protected void registeringItem(PandaPluginClass plugin, YamlSection itemSection) {
		if (!itemSection.getName().equals("Entity")) {
			super.registeringItem(plugin, itemSection);
		}
	}

}
