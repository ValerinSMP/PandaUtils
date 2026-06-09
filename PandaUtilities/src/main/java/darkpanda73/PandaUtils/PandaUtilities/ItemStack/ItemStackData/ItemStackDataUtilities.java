package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTAbstract;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv1;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv2;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv3;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv4;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv5;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.NBT.NMSNBTv6;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.PersistentDataContainer.NMSPersistentDataContainerAbstract;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.PersistentDataContainer.NMSPersistentDataContainer_V1;

/**
 * Utility class used to set or retrieve persistent data from an item.
 * 
 * @author Darkpanda73
 *
 */
public class ItemStackDataUtilities {

	private static NMSNBTAbstract NMS_NBT;
	private static NMSPersistentDataContainerAbstract NMS_PERSISTENTDATACONTAINER;

	static {
		List<Exception> exceptions = new ArrayList<>();
		for (Class<? extends NMSNBTAbstract> clazz : Arrays.asList(NMSNBTv1.class, NMSNBTv2.class, NMSNBTv3.class,
				NMSNBTv4.class, NMSNBTv5.class, NMSNBTv6.class)) {
			try {
				NMS_NBT = clazz.getConstructor().newInstance();
				break;
			} catch (Exception e) {
				NMS_NBT = null;
				exceptions.add(e);
			}
		}

		for (Class<? extends NMSPersistentDataContainerAbstract> clazz : Arrays
				.asList(NMSPersistentDataContainer_V1.class)) {
			try {
				NMS_PERSISTENTDATACONTAINER = clazz.getConstructor().newInstance();
				break;
			} catch (Exception e) {
				NMS_PERSISTENTDATACONTAINER = null;
				exceptions.add(e);
			}
		}

		if (NMS_NBT == null && NMS_PERSISTENTDATACONTAINER == null && !exceptions.isEmpty()) {
			System.err.println("[ItemStackDataUtilities] Unable to load NMS data:");
			exceptions.get(exceptions.size() - 1).printStackTrace();
		} else {
			if (NMS_NBT != null) {
				System.out.println(
						"[PandaUtilities] Initialized NBT methods for itemstack data persistance successfully.");
			} else if (NMS_PERSISTENTDATACONTAINER != null) {
				System.out.println(
						"[PandaUtilities] Initialized PersistentDataContainer methods for itemstack data persistance successfully.");
			} else {
				System.out.println("[PandaUtilities] Unable to retrieve methods for itemstack data persistance.");
			}
		}
	}

	public static NMSNBTAbstract getNmsNBT() {
		return NMS_NBT;
	}

	/**
	 * Method used to set a persistent data into an item. This method is used for
	 * the basic persistent data system with NBT which is compatible with 1.8.8+
	 * versions. In case to use another system compatible with higher versions, the
	 * PersistentDataContainer, use the other method.
	 * 
	 * @param item  ItemStack to set the persistent data.
	 * @param key   Key which the value should be associated.
	 * @param value The value to be persisted.
	 * @return The modified item. This reference is returned in the method as NBT
	 *         generates a new copy with the desired persistent data.
	 * @throws Exception In case any error occurs while setting the information.
	 */
	public static ItemStack setPersistentData(ItemStack item, String key, Object value) throws Exception {
		return setPersistentData(item, null, key, value);
	}

	/**
	 * Method used to set a persistent data into an item.
	 * 
	 * @param item   ItemStack to set the persistent data.
	 * @param plugin Plugin associated to this persistent data.
	 * @param values Map with the values to set on the item.
	 * @return The modified item. This reference is returned in the method as NBT
	 *         generates a new copy with the desired persistent data.
	 * @throws Throwable
	 */
	public static ItemStack setPersistentData(ItemStack item, Plugin plugin, Map<String, Object> values)
			throws Throwable {
		if (item == null) {
			return null;
		}

		if (NMS_PERSISTENTDATACONTAINER != null && plugin != null) {
			return NMS_PERSISTENTDATACONTAINER.setMultiple(item, plugin, values);
		}

		if (NMS_NBT != null) {
			Object nmsStack = NMS_NBT.craftItemStackAsNMSCopy(item);
			Object compound = NMS_NBT.itemStackHasTag(nmsStack) ? NMS_NBT.itemStackGetTag(nmsStack)
					: NMS_NBT.nbtTagCompoundNew();
			for (Entry<String, Object> entry : values.entrySet()) {
				NMS_NBT.nbtTagCompoundSetString(compound, entry.getKey(), entry.getValue().toString());
			}
			NMS_NBT.itemStackSetTag(nmsStack, compound);
			item = (ItemStack) NMS_NBT.craftItemStackAsBukkitCopy(nmsStack);

			return item;
		}

		throw new Exception("No NMS utility available to set persistent data on ItemStacks");
	}

	/**
	 * Method used to set a persistent data into an item.
	 * 
	 * @param item   ItemStack to set the persistent data.
	 * @param plugin Plugin associated to this persistent data.
	 * @param key    Key which the value should be associated.
	 * @param value  The value to be persisted.
	 * @return The modified item. This reference is returned in the method as NBT
	 *         generates a new copy with the desired persistent data.
	 * @throws Exception In case any error occurs while setting the information.
	 */
	public static ItemStack setPersistentData(ItemStack item, Plugin plugin, String key, Object value)
			throws Exception {
		if (item == null) {
			return null;
		}

		if (NMS_PERSISTENTDATACONTAINER != null && plugin != null) {
			return NMS_PERSISTENTDATACONTAINER.set(item, plugin, key, value);
		}

		if (NMS_NBT != null) {
			Object nmsStack = NMS_NBT.craftItemStackAsNMSCopy(item);
			Object compound = NMS_NBT.itemStackHasTag(nmsStack) ? NMS_NBT.itemStackGetTag(nmsStack)
					: NMS_NBT.nbtTagCompoundNew();
			NMS_NBT.nbtTagCompoundSetString(compound, key, value.toString());
			NMS_NBT.itemStackSetTag(nmsStack, compound);
			item = (ItemStack) NMS_NBT.craftItemStackAsBukkitCopy(nmsStack);

			return item;
		}

		throw new Exception("No NMS utility available to set persistent data on ItemStacks");
	}

	/**
	 * Method used to retrieve the current value of a persisted data in the given
	 * item. This method is used for the basic persistent data system with NBT which
	 * is compatible with 1.8.8+ versions. In case to use another system compatible
	 * with higher versions, the PersistentDataContainer, use the other method.
	 * 
	 * @param item ItemStack to set the persistent data.
	 * @param key  Key which the value should be associated.
	 * @return The current value of the key in the item, or null if the item is null
	 *         or an exception occurs.
	 * @throws Exception In case any error occurs while setting the information.
	 */
	public static <T> T getPersistentData(ItemStack item, String key, Class<T> valueClass) throws Exception {
		return getPersistentData(item, null, key, valueClass);
	}

	/**
	 * Method used to retrieve the current value of a persisted data in the given
	 * item.
	 * 
	 * @param item   ItemStack to retrieve the persistent data.
	 * @param plugin Plugin associated to this persistent data.
	 * @param key    Key which the value is associated.
	 * @return The current value of the key in the item, or null if the item is null
	 *         or an exception occurs.
	 * @throws Exception In case any error occurs while setting the information.
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getPersistentData(ItemStack item, Plugin plugin, String key, Class<T> valueClass)
			throws Exception {
		if (item == null) {
			return null;
		}

		if (NMS_PERSISTENTDATACONTAINER != null && plugin != null) {
			return NMS_PERSISTENTDATACONTAINER.get(item, plugin, key, valueClass);
		}

		if (NMS_NBT != null) {
			Object nmsStack = NMS_NBT.craftItemStackAsNMSCopy(item);

			if (NMS_NBT.itemStackHasTag(nmsStack)) {
				return (T) NMS_NBT.nbtTagCompoundGetString(NMS_NBT.itemStackGetTag(nmsStack), key);
			}

			return null;
		}

		throw new Exception("No NMS utility available to retrieve persistent data on ItemStacks");
	}

	/**
	 * Enum used to offer which types of data can be set as a persistent data. This
	 * is later used to know which type of data should be used, and then to
	 * translate this enum into the respective {@link PersistentDataType}.
	 * 
	 * @author DarkPanda73
	 *
	 */
	public static enum DataType {
		BYTE, SHORT, INTEGER, LONG, FLOAT, DOUBLE, STRING
	}

}
