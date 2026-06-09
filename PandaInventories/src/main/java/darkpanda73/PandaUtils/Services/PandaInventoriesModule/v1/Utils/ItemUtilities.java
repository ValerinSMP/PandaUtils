package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;

public class ItemUtilities {

	public static final String SLOT_SINGLE_REGEX = "[0-9]+";
	public static final String SLOT_RANGE_REGEX = "[0-9]+-[0-9]+";

	private static final ItemStack PLAYER_HEAD_ITEMSTACK;

	static {
		ItemStack playerHeadItemStack;

		try {
			playerHeadItemStack = ItemBuilder.inst().setMaterial("PLAYER_HEAD").build();
		} catch (IllegalArgumentException e) {
			playerHeadItemStack = ItemBuilder.inst().setMaterial("SKULL_ITEM").setMaterialData((short) 3).build();
		}

		PLAYER_HEAD_ITEMSTACK = playerHeadItemStack;
	}

	public static Material getPlayerHeadMaterial() {
		return PLAYER_HEAD_ITEMSTACK.getType();
	}

	public static ItemStack createPlayerHead() {
		return PLAYER_HEAD_ITEMSTACK.clone();
	}

	public static ItemStack itemBySection(Item item, String section) {
		return itemBySection(null, item, section);
	}

	public static ItemStack itemBySection(ItemStack originalItemStack, Item item, String section) {
		return ItemBuilder.inst().fromItem(originalItemStack).fromMap(item.getData(), section).build();
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

}
