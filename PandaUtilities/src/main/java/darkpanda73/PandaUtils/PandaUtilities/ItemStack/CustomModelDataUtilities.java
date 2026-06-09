package darkpanda73.PandaUtils.PandaUtilities.ItemStack;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import lombok.NonNull;

public class CustomModelDataUtilities {

	public static final MethodResult ITEMMETA_HASCUSTOMMODELDATA_METHOD = MethodFinder.forClass(ItemMeta.class)
			.setName("hasCustomModelData").findSafely();
	public static final MethodResult ITEMMETA_GETCUSTOMMODELDATA_METHOD = MethodFinder.forClass(ItemMeta.class)
			.setName("getCustomModelData").findSafely();
	public static final MethodResult ITEMMETA_SETCUSTOMMODELDATA_METHOD = MethodFinder.forClass(ItemMeta.class)
			.setName("setCustomModelData").setParams(Integer.class).findSafely();

	public static Integer getCustomModelData(ItemStack item) {
		return item != null && item.hasItemMeta() ? getCustomModelData(item.getItemMeta()) : null;
	}

	public static Integer getCustomModelData(@NonNull ItemMeta itemMeta) {
		if (ITEMMETA_GETCUSTOMMODELDATA_METHOD.isPresent() && ITEMMETA_HASCUSTOMMODELDATA_METHOD.isPresent()
				&& Boolean.TRUE.equals(ITEMMETA_HASCUSTOMMODELDATA_METHOD.invokeSafely(itemMeta))) {
			return ITEMMETA_GETCUSTOMMODELDATA_METHOD.invokeSafely(itemMeta);
		}
		return null;
	}

	public static void setCustomModelData(ItemStack item, Integer customModelData) {
		if (item != null && item.hasItemMeta()) {
			setCustomModelData(item.getItemMeta(), customModelData);
		}
	}

	public static void setCustomModelData(@NonNull ItemMeta itemMeta, Integer customModelData) {
		if (ITEMMETA_SETCUSTOMMODELDATA_METHOD.isPresent()) {
			ITEMMETA_SETCUSTOMMODELDATA_METHOD.invokeSafely(itemMeta, customModelData);
		}
	}

}
