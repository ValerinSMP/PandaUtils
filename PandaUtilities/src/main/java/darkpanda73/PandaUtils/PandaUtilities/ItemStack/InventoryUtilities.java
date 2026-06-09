package darkpanda73.PandaUtils.PandaUtilities.ItemStack;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder.ClassResult;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class InventoryUtilities {

	public static final ClassResult INVENTORYVIEW_CLASS = ClassFinder.forClass("org.bukkit.inventory.InventoryView")
			.find();
	public static final MethodResult PLAYER_GETOPENINVENTORY_METHOD = MethodFinder.forClass(HumanEntity.class)
			.setName("getOpenInventory").find();
	public static final MethodResult INVENTORYVIEW_GETTOPINVENTORY_METHOD = MethodFinder
			.forClass(INVENTORYVIEW_CLASS.getFoundClass()).setName("getTopInventory").find();
	public static final MethodResult INVENTORYVIEW_SETCURSOR_METHOD = MethodFinder
			.forClass(INVENTORYVIEW_CLASS.getFoundClass()).setName("setCursor").setParams(ItemStack.class).find();
	public static final MethodResult INVENTORYVIEW_GETTITLE_METHOD = MethodFinder
			.forClass(INVENTORYVIEW_CLASS.getFoundClass()).setName("getTitle").findSafely();

	public static Object getOpenInventory(HumanEntity player) {
		return PLAYER_GETOPENINVENTORY_METHOD.invokeSafely(player);
	}

	public static void setCursor(HumanEntity player, ItemStack itemStack) {
		Object inventoryView = PLAYER_GETOPENINVENTORY_METHOD.invokeSafely(player);
		if (inventoryView != null) {
			INVENTORYVIEW_SETCURSOR_METHOD.invokeSafely(inventoryView, itemStack);
		}
	}

	public static Inventory getTopInventory(HumanEntity player) {
		Object inventoryView = PLAYER_GETOPENINVENTORY_METHOD.invokeSafely(player);
		if (inventoryView != null) {
			return INVENTORYVIEW_GETTOPINVENTORY_METHOD.invokeSafely(inventoryView);
		}
		return null;
	}

	public static String getTitle(HumanEntity player) {
		Object inventoryView = PLAYER_GETOPENINVENTORY_METHOD.invokeSafely(player);
		if (inventoryView != null) {
			return INVENTORYVIEW_GETTITLE_METHOD.invokeSafely(inventoryView);
		}
		return null;
	}

}
