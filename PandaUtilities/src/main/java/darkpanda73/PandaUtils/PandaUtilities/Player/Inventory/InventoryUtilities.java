package darkpanda73.PandaUtils.PandaUtilities.Player.Inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class InventoryUtilities {

	private static MethodResult GET_ITEMINMAINHAND;
	private static MethodResult GET_ITEMINOFFHAND;
	private static MethodResult SET_ITEMINMAINHAND;
	private static MethodResult SET_ITEMINOFFHAND;

	static {
		try {
			GET_ITEMINMAINHAND = MethodFinder.forClass(PlayerInventory.class).setName("getItemInMainHand").find();
			GET_ITEMINOFFHAND = MethodFinder.forClass(PlayerInventory.class).setName("getItemInOffHand").find();
			SET_ITEMINMAINHAND = MethodFinder.forClass(PlayerInventory.class).setName("setItemInMainHand")
					.setParams(ItemStack.class).find();
			SET_ITEMINOFFHAND = MethodFinder.forClass(PlayerInventory.class).setName("setItemInOffHand")
					.setParams(ItemStack.class).find();
		} catch (Exception e) {
			try {
				GET_ITEMINMAINHAND = MethodFinder.forClass(PlayerInventory.class).setName("getItemInHand").find();
				GET_ITEMINOFFHAND = null;
				SET_ITEMINMAINHAND = MethodFinder.forClass(PlayerInventory.class).setName("setItemInHand")
						.setParams(ItemStack.class).find();
				SET_ITEMINOFFHAND = null;
			} catch (Exception e2) {
				GET_ITEMINMAINHAND = null;
				GET_ITEMINOFFHAND = null;
				SET_ITEMINMAINHAND = null;
				SET_ITEMINOFFHAND = null;
			}
		}
	}

	public static ItemStack getItemByHand(Player pl, EquipmentSlot hand, ItemStack defaultItem) {
		if (hand != null) {
			switch (hand.name()) {
			case "HAND":
				return InventoryUtilities.getItemInMainHand(pl);
			case "OFF_HAND":
				return InventoryUtilities.getItemInOffHand(pl);
			}
		}
		return defaultItem;
	}

	public static ItemStack getItemInMainHand(Player pl) {
		try {
			if (GET_ITEMINMAINHAND != null)
				return (ItemStack) GET_ITEMINMAINHAND.invoke(pl.getInventory());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ItemStack getItemInOffHand(Player pl) {
		try {
			if (GET_ITEMINOFFHAND != null)
				return (ItemStack) GET_ITEMINOFFHAND.invoke(pl.getInventory());
		} catch (Exception e) {
		}
		return null;
	}

	public static void setItemInMainHand(Player pl, ItemStack item) {
		try {
			if (SET_ITEMINMAINHAND != null)
				SET_ITEMINMAINHAND.invoke(pl.getInventory(), item);
		} catch (Exception e) {
		}
	}

	public static void setItemInOffHand(Player pl, ItemStack item) {
		try {
			if (SET_ITEMINOFFHAND != null)
				SET_ITEMINOFFHAND.invoke(pl.getInventory(), item);
		} catch (Exception e) {
		}
	}

}
