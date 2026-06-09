package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringJoiner;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaDefaultInventoryCommand;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaInventoryItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects.PandaInventorySettings;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class InventoryUtilities {

	public static final String SLOT_SINGLE_REGEX = "[0-9]+";
	public static final String SLOT_RANGE_REGEX = "[0-9]+-[0-9]+";

	public static GeneratedInventory fileToInventory(File file)
			throws FileNotFoundException, YamlException, IOException {
		PandaYaml yaml = new PandaYaml(file);

		int version = yaml.getRoot().getDataOrDefault(PandaInventorySettings.VERSION_KEY, 0).getInteger();

		PandaInventorySettings settings = new PandaInventorySettings(file.getName().replaceAll("\\..*", ""), version);

		settings.setSize(yaml.getRoot().getDataOrDefault(PandaInventorySettings.SIZE_KEY, 54).getInteger());
		settings.setApplyPlaceholderApi(
				yaml.getRoot().getDataOrDefault(PandaInventorySettings.APPLY_PLACEHOLDERAPI_KEY, false).getBoolean());

		YamlSection titles = yaml.getRoot().getSection(PandaInventorySettings.TITLES_KEY);

		if (titles != null) {
			titles.getNamedChilds().forEach(child -> {
				if (child.isYamlData()) {
					settings.getTitles().put(child.getName().toLowerCase(), child.asYamlData().getString());
				}
			});
		}

		YamlSection data = yaml.getRoot().getSection(PandaInventorySettings.DATA_KEY);

		if (data != null) {
			settings.getData().putAll(data.toMap());
		}

		YamlSection items = yaml.getRoot().getSection(PandaInventorySettings.ITEMS_KEY);

		if (items != null) {
			items.getNamedChilds().forEach(child -> {
				if (child.isYamlSection()) {
					YamlSection itemSection = child.asYamlSection();

					PandaInventoryItem item = new PandaInventoryItem();

					item.getPlayerCommands()
							.addAll(itemSection
									.getDataOrDefault(PandaInventoryItem.PLAYER_COMMANDS_KEY, item.getPlayerCommands())
									.getStringList());
					item.getConsoleCommands().addAll(itemSection
							.getDataOrDefault(PandaInventoryItem.CONSOLE_COMMANDS_KEY, item.getConsoleCommands())
							.getStringList());
					item.setUpdateEveryXTicks(itemSection
							.getDataOrDefault(PandaInventoryItem.UPDATE_EVERY_X_TICKS_KEY, item.getUpdateEveryXTicks())
							.getLong());
					item.setApplyPlaceholderAPI(itemSection
							.getDataOrDefault(PandaInventoryItem.APPLY_PLACEHOLDERAPI_KEY, item.isApplyPlaceholderAPI())
							.getBoolean());
					item.setCloseInventoryOnClick(
							itemSection.getDataOrDefault(PandaInventoryItem.CLOSE_INVENTORY_ON_CLICK_KEY,
									item.isCloseInventoryOnClick()).getBoolean());
					item.setSlots(
							stringToSlots(itemSection.getDataOrDefault(PandaInventoryItem.SLOTS_KEY, "").getString()));

					YamlData<?> permissionData = itemSection.getData(PandaInventoryItem.PERMISSION_KEY);

					if (permissionData != null) {
						item.setPermission(permissionData.getString());
					}

					YamlSection itemStacksSection = itemSection.getSection(PandaInventoryItem.ITEMS_KEY);

					if (itemStacksSection != null) {
						itemStacksSection.getNamedChilds().forEach(itemStackChild -> {
							if (itemStackChild.isYamlSection()) {
								item.setItem(itemStackChild.getName(),
										ItemBuilder.inst().fromMap(itemStackChild.asYamlSection().toMap()).build());
							}
						});
					}
				}
			});
		}

		YamlData<?> commandName = yaml.getRoot().getData(PandaInventorySettings.COMMAND_NAME_KEY);
		YamlData<?> commandDescription = yaml.getRoot().getData(PandaInventorySettings.COMMAND_DESCRIPTION_KEY);
		YamlData<?> commandAliases = yaml.getRoot().getDataOrDefault(PandaInventorySettings.COMMAND_ALIASES_KEY,
				Collections.emptyList());

		return new GeneratedInventory(settings,
				(commandName != null && commandDescription != null)
						? new PandaDefaultInventoryCommand(commandName.getString(), commandDescription.getString(),
								commandAliases.getStringList())
						: null);
	}

	public static int[] stringToSlots(String slotsTemplate) {
		if (slotsTemplate != null && !slotsTemplate.equals("-1")) {
			String[] split = slotsTemplate.split(";");
			List<Integer> slots = new ArrayList<>();
			for (String slot : split) {
				if (slot.matches(SLOT_SINGLE_REGEX)) {
					slots.add(Integer.parseInt(slot));
				} else if (slot.matches(SLOT_RANGE_REGEX)) {
					String[] slotSplit = slot.split("-");
					int max = Integer.parseInt(slotSplit[1]);
					for (int i = Integer.parseInt(slotSplit[0]); i <= max; i++) {
						slots.add(i);
					}
				}
			}

			return slots.stream().mapToInt(i -> i).toArray();
		} else {
			return new int[0];
		}
	}

	public static String slotsToString(int[] slots) {
		StringJoiner joiner = new StringJoiner(";");

		int initialSlot = -1;
		int previousSlot = -1;

		for (int i = 0; i < slots.length; i++) {
			if (initialSlot == -1) {
				initialSlot = slots[i];
				previousSlot = slots[i];

				continue;
			}

			if (slots[i] != previousSlot + 1) {
				joiner.add(
						initialSlot != previousSlot ? (initialSlot + "-" + previousSlot) : String.valueOf(initialSlot));

				initialSlot = slots[i];
			}

			previousSlot = slots[i];
		}

		if (initialSlot != -1) {
			joiner.add(initialSlot != previousSlot ? (initialSlot + "-" + previousSlot) : String.valueOf(initialSlot));
		}

		return joiner.toString();
	}

	@AllArgsConstructor
	public static class GeneratedInventory {

		private @Getter PandaInventorySettings inventory;
		private @Getter PandaDefaultInventoryCommand command;

	}

}
