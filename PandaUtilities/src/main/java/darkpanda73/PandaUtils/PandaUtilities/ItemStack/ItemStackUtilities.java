package darkpanda73.PandaUtils.PandaUtilities.ItemStack;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Enum.EnumUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class ItemStackUtilities {

	public static final Material PLAYER_HEAD_MATERIAL = EnumUtilities.findByAny(Material.class, "PLAYER_HEAD",
			"SKULL_ITEM");
	public static final MethodResult GET_DURABILITY_METHOD = EnumUtilities.findByAny(Material.class,
			"SKULL_ITEM") != null
					? MethodFinder.forClass(ItemStack.class).setName("getDurability").setReturnType(short.class).find()
					: MethodResult.EMPTY;
	public static final MethodResult DURABILITY_METHOD = EnumUtilities.findByAny(Material.class, "SKULL_ITEM") != null
			? MethodFinder.forClass(ItemStack.class).setName("setDurability").setParams(short.class).find()
			: null;

	public static final String TYPE_KEY = "Type";
	public static final String AMOUNT_KEY = "Amount";
	public static final String NAME_KEY = "Name";
	public static final String LORE_KEY = "Lore";
	public static final String SKIN_KEY = "Skin";
	public static final String SKINURL_KEY = "Skin-URL";
	public static final String CUSTOMMODELDATA_KEY = "Custom-model-data";
	public static final String GLOW_KEY = "Glow";

	public static final Enchantment UNBREAKING_ECHANTMENT;

	private static final BiConsumer<SkullMeta, OfflinePlayer> SET_SKIN_METHOD;

	static {
		MethodResult enchantmentGetMethod = MethodFinder.forClass(Enchantment.class).setName("getEnchantment")
				.findSafely();
		if (enchantmentGetMethod.isPresent()) {
			Enchantment unbreaking = null;

			try {
				unbreaking = enchantmentGetMethod.invoke(null, "durability");
			} catch (Throwable e) {
				try {
					unbreaking = enchantmentGetMethod.invoke(null, "unbreaking");
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
					System.out.println("Unable to retrieve unbreaking enchantment from Spigot API");
					e1.printStackTrace();
				}
			}

			UNBREAKING_ECHANTMENT = unbreaking;
		} else {
			enchantmentGetMethod = MethodFinder.forClass(Enchantment.class).setName("getByName").findSafely();
			if (enchantmentGetMethod.isPresent()) {
				UNBREAKING_ECHANTMENT = enchantmentGetMethod.invokeSafelyWithStackTrace(null, "DURABILITY");
			} else {
				UNBREAKING_ECHANTMENT = null;
			}
		}

		MethodResult method = MethodFinder.forClass(SkullMeta.class).setName("setOwningPlayer")
				.setParams(OfflinePlayer.class).findSafely();
		if (method.isPresent()) {
			MethodResult fMethod = method;
			SET_SKIN_METHOD = (meta, pl) -> fMethod.invokeSafelyWithStackTrace(meta, pl);
		} else {
			method = MethodFinder.forClass(SkullMeta.class).setName("setOwner").setParams(String.class).findSafely();
			if (method.isPresent()) {
				MethodResult fMethod = method;
				SET_SKIN_METHOD = (meta, pl) -> fMethod.invokeSafelyWithStackTrace(meta,
						pl != null ? pl.getName() : "");
			} else {
				SET_SKIN_METHOD = (meta, pl) -> {
				};
			}
		}
	}

	public static Map<String, Object> itemToMap(ItemStack item) {
		HashMap<String, Object> data = new LinkedHashMap<>();

		ItemMeta im = item.getItemMeta();

		data.put(TYPE_KEY, item.getType().name());
		data.put(AMOUNT_KEY, item.getAmount());
		if (im.hasDisplayName()) {
			data.put(NAME_KEY, im.getDisplayName());
		}
		if (im.hasLore()) {
			data.put(LORE_KEY, im.getLore());
		}

		if (im instanceof SkullMeta) {
			String skin = SkinUtilities.NMS.getSkinAsBase64(item);
			if (skin != null) {
				data.put(SKIN_KEY, skin);
			}
		}

		Integer customModelData = CustomModelDataUtilities.getCustomModelData(im);
		if (customModelData != null) {
			data.put(CUSTOMMODELDATA_KEY, customModelData);
		}

		return data;
	}

	public static ItemStack mapToItem(Map<String, Object> map) throws Exception {
		return mapToItem(map, true);
	}

	public static ItemStack mapToItem(Map<String, Object> map, boolean processColor) throws Exception {
		return mapToItem(null, map, processColor);
	}

	@SuppressWarnings("unchecked")
	public static ItemStack mapToItem(ItemStack originalItem, Map<String, Object> map, boolean processColor)
			throws Exception {
		if (map.isEmpty()) {
			return null;
		}

		ItemStack item = null;

		if (originalItem != null) {
			item = originalItem;
			item.setType(map.containsKey(TYPE_KEY) ? Material.valueOf((String) map.get(TYPE_KEY)) : item.getType());
			item.setAmount(map.containsKey(AMOUNT_KEY) ? ((Number) map.get(AMOUNT_KEY)).intValue() : item.getAmount());
		} else {
			Material material = Material.valueOf((String) map.get(TYPE_KEY));
			item = new ItemStack(material, map.containsKey(AMOUNT_KEY) ? ((Number) map.get(AMOUNT_KEY)).intValue() : 1);
		}

		ItemMeta im = item.getItemMeta();

		if (map.containsKey(NAME_KEY)) {
			im.setDisplayName(processColor ? MessageTemplate.inst((String) map.get(NAME_KEY)).process().toString()
					: (String) map.get(NAME_KEY));
		}

		if (map.containsKey(LORE_KEY)) {
			im.setLore(processColor ? MessageTemplate.inst((List<String>) map.get(LORE_KEY)).process().getStrings()
					: (List<String>) map.get(LORE_KEY));
		}

		if (map.containsKey(CUSTOMMODELDATA_KEY)) {
			CustomModelDataUtilities.setCustomModelData(im,
					map.get(CUSTOMMODELDATA_KEY) != null ? ((Number) map.get(CUSTOMMODELDATA_KEY)).intValue() : null);
		}

		item.setItemMeta(im);

		if (im instanceof SkullMeta) {
			String skin = map.containsKey(SKIN_KEY) ? (String) map.get(SKIN_KEY) : null;
			SkinUtilities.NMS.setSkin(item, skin, null);
		}

		if (map.containsKey(GLOW_KEY) && Boolean.TRUE) {
			if (hasGlowEffect(item) != Boolean.TRUE.equals(((Boolean) map.get(GLOW_KEY)))) {
				switchGlowEffect(item);
			}
		}

		return item;
	}

	public static ItemStack setReplacements(ItemStack item, Replacement[] replacements) {
		ItemMeta im = item.getItemMeta();

		if (im.hasDisplayName()) {
			im.setDisplayName(
					MessageTemplate.inst(im.getDisplayName()).setReplacements(replacements).process().toString());
		}

		if (im.hasLore()) {
			im.setLore(MessageTemplate.inst(im.getLore().toArray(new String[im.getLore().size()]))
					.setReplacements(replacements).process().getStrings());
		}

		item.setItemMeta(im);

		return item;
	}

	public static void switchGlowEffect(ItemStack item) {
		if (item != null) {
			ItemMeta im = item.getItemMeta();
			if (im.getEnchants().isEmpty()) {
				im.addEnchant(UNBREAKING_ECHANTMENT, 1, false);
				im.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			} else {
				im.getEnchants().keySet().forEach(im::removeEnchant);
				im.removeItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
			}
			item.setItemMeta(im);
		}
	}

	public static boolean hasGlowEffect(ItemStack item) {
		return (item != null && item.hasItemMeta() && item.getItemMeta().hasEnchant(UNBREAKING_ECHANTMENT)
				&& item.getItemMeta().hasItemFlag(ItemFlag.HIDE_ENCHANTS));
	}

	public static ItemStack getPlayerHead(UUID uuid) {
		return getPlayerHead(() -> OfflinePlayerUtilities.getOfflinePlayer(uuid));
	}

	public static ItemStack getPlayerHead(OfflinePlayer pl) {
		return getPlayerHead(() -> pl);
	}

	public static ItemStack getPlayerHead(Supplier<OfflinePlayer> plSupplier) {
		ItemStack playerHead = new ItemStack(PLAYER_HEAD_MATERIAL);

		if (DURABILITY_METHOD != null) {
			DURABILITY_METHOD.invokeSafely(playerHead, 3);
		}

		return getPlayerHead(playerHead, plSupplier);
	}

	public static ItemStack getPlayerHead(ItemStack head, UUID uuid) {
		return getPlayerHead(head, () -> OfflinePlayerUtilities.getOfflinePlayer(uuid));
	}

	public static ItemStack getPlayerHead(ItemStack head, OfflinePlayer pl) {
		return getPlayerHead(head, () -> pl);
	}

	public static ItemStack getPlayerHead(ItemStack head, Supplier<OfflinePlayer> plSupplier) {
		TasksUtils.executeOnAsync(() -> getPlayerHeadSync(head, plSupplier));
		return head;
	}

	public static ItemStack getPlayerHeadSync(ItemStack head, UUID uuid) {
		return getPlayerHeadSync(head, () -> OfflinePlayerUtilities.getOfflinePlayer(uuid));
	}

	public static ItemStack getPlayerHeadSync(ItemStack head, OfflinePlayer pl) {
		return getPlayerHeadSync(head, () -> pl);
	}

	public static ItemStack getPlayerHeadSync(ItemStack head, Supplier<OfflinePlayer> plSupplier) {
		SkullMeta im = (SkullMeta) head.getItemMeta();
		if (im != null) {
			OfflinePlayer pl = plSupplier.get();
			if (pl != null && pl.getName() != null) {
				SET_SKIN_METHOD.accept(im, pl);
				head.setItemMeta(im);
			}
		}
		return head;
	}

	public static byte[] itemsParse(ItemStack[] items) {
		try {
			return unsafeItemsParse(items);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new byte[0];
	}

	public static byte[] unsafeItemsParse(ItemStack[] items) throws IOException {
		ByteArrayOutputStream str = new ByteArrayOutputStream();
		BukkitObjectOutputStream data = new BukkitObjectOutputStream(str);
		data.writeInt(items.length);
		for (ItemStack item : items) {
			data.writeObject(item);
		}
		data.close();
		return str.toByteArray();
	}

	public static ItemStack[] itemsParse(byte[] items) {
		return itemsParse(items, new ItemStack[0]);
	}

	public static ItemStack[] itemsParse(byte[] items, ItemStack[] defaultItems) {
		if (items != null)
			try {
				return unsafeItemsParse(items);
			} catch (Exception e) {
				e.printStackTrace();
			}
		return defaultItems;
	}

	public static ItemStack[] unsafeItemsParse(byte[] items) throws IOException, ClassNotFoundException {
		ByteArrayInputStream stream = new ByteArrayInputStream(items);
		BukkitObjectInputStream data = new BukkitObjectInputStream(stream);
		ItemStack[] its = new ItemStack[data.readInt()];
		for (int i = 0; i < its.length; i++) {
			its[i] = (ItemStack) data.readObject();
			if (its[i] != null && its[i].getType() == Material.AIR) {
				its[i] = null;
			}
		}
		data.close();
		return its;
	}

	public static boolean hasAvailableSpace(Player pl, ItemStack... items) {
		if (pl.getInventory().firstEmpty() != -1) {
			return true;
		} else {
			if (Arrays.stream(items).allMatch(item -> pl.getInventory().containsAtLeast(item, 1))) {
				HashMap<Integer, ItemStack> missingItems = pl.getInventory().addItem(items);

				if (missingItems.isEmpty()) {
					pl.getInventory().removeItem(items);
					return true;
				} else {
					for (int i = 0; i < items.length; i++) {
						if (missingItems.containsKey(i)) {
							int originalAmount = items[i].getAmount();
							items[i].setAmount(missingItems.get(i).getAmount());
							pl.getInventory().removeItem(items[i]);
							items[i].setAmount(originalAmount);
						} else {
							pl.getInventory().removeItem(items[i]);
						}
					}
				}
			}
		}

		return false;
	}

	public static void giveOrDrop(Player pl, ItemStack... items) {
		pl.getInventory().addItem(items)
				.forEach((index, item) -> pl.getLocation().getWorld().dropItem(pl.getLocation(), item));
	}

	public static boolean isSame(ItemStack itemStack, Material material) {
		return itemStack != null && itemStack.getType() == material;
	}

	public static void removeItem(Player pl, Material material) {
		if (pl.getOpenInventory() != null && isSame(pl.getOpenInventory().getCursor(), material)) {
			pl.getOpenInventory().setCursor(null);
		}

		if (isSame(pl.getInventory().getItemInOffHand(), material)) {
			pl.getInventory().setItemInOffHand(null);
		}

		pl.getInventory().remove(material);
	}

	public static void removeItem(Player pl, Function<ItemStack, Boolean> condition) {
		if (pl.getOpenInventory() != null && condition.apply(pl.getOpenInventory().getCursor())) {
			pl.getOpenInventory().setCursor(null);
		}

		if (condition.apply(pl.getInventory().getItemInOffHand())) {
			pl.getInventory().setItemInOffHand(null);
		}

		List<ItemStack> itemsToRemove = new ArrayList<>();
		for (ItemStack i : pl.getInventory().getContents()) {
			if (condition.apply(i)) {
				itemsToRemove.add(i);
			}
		}

		pl.getInventory().removeItem(itemsToRemove.toArray(new ItemStack[itemsToRemove.size()]));
	}

	public static void reduceItemInInventory(Player pl, ItemStack item, int amountToTake) {
		reduceItemInInventory(pl, item, amountToTake, (Integer) null);
	}

	public static void reduceItemInInventory(Player pl, ItemStack item, int amountToTake, Integer slotIndex) {
		reduceItemInInventory(pl, item, amountToTake, slotIndex, true);
	}

	public static void reduceItemInInventory(Player pl, ItemStack item, int amountToTake, Integer slotIndex,
			boolean ignoreInCreative) {
		if (pl.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (item.getAmount() > amountToTake) {
			item.setAmount(item.getAmount() - amountToTake);
		} else {
			if (slotIndex != null) {
				pl.getInventory().setItem(slotIndex, null);
			} else {
				pl.getInventory().removeItem(item);
			}
		}
	}

	public static void reduceItemInInventory(Player pl, ItemStack item, int amountToTake, EquipmentSlot hand) {
		reduceItemInInventory(pl, item, amountToTake, hand, true);
	}

	public static void reduceItemInInventory(Player pl, ItemStack item, int amountToTake, EquipmentSlot hand,
			boolean ignoreInCreative) {
		if (pl.getGameMode() == GameMode.CREATIVE) {
			return;
		}

		if (item.getAmount() > amountToTake) {
			item.setAmount(item.getAmount() - amountToTake);
		} else {
			if (hand != null) {
				pl.getInventory().setItem(hand, null);
			} else {
				pl.getInventory().removeItem(item);
			}
		}
	}

	public static boolean isSimilar(ItemStack item1, ItemStack item2) {
		if (item1 == item2) {
			return true;
		}

		if ((item1 == null) != (item2 == null)) {
			return false;
		}

		if (item1.getType() != item2.getType()) {
			return false;
		}

		if (item1.hasItemMeta() != item2.hasItemMeta()) {
			return false;
		}

		try {
			return item1.getItemMeta().equals(item2.getItemMeta());
		} catch (Exception e) {
			return false;
		}
	}

}
