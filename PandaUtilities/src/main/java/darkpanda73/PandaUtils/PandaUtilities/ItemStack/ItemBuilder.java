package darkpanda73.PandaUtils.PandaUtilities.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ColorMode;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.Objects.RGBColor;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.ItemStackDataUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Enum.EnumUtilities;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

@Accessors(chain = true)
@Getter
@Setter
@Data
@ToString(onlyExplicitlyIncluded = true)
public class ItemBuilder {

	public static final Constructor<ItemStack> ITEMSTACK_WITH_MATERIAL_DATA_CONTRUCTOR;
	public static final ItemFlag[] DEFAULT_FLAGS;
	public static final ItemFlag HIDE_ITEM_NAME_FLAG;

	static {
		Constructor<ItemStack> constructor = null;

		try {
			ItemStack.class.getConstructor(Material.class, int.class, short.class);
		} catch (Throwable e) {
			// Ignoring error
		}

		ITEMSTACK_WITH_MATERIAL_DATA_CONTRUCTOR = constructor;

		List<ItemFlag> flags = Arrays
				.stream(ItemFlag.values()).filter(flag -> !(flag.name().equals("HIDE_LORE")
						|| flag.name().equals("HIDE_ITEM_NAME") || flag.name().equals("HIDE_CUSTOM_NAME")))
				.collect(Collectors.toList());

		DEFAULT_FLAGS = flags.toArray(new ItemFlag[flags.size()]);

		ItemFlag flag = null;

		try {
			flag = ItemFlag.valueOf("HIDE_ITEM_NAME");
		} catch (Throwable e) {
			// Ignoring error
		}

		HIDE_ITEM_NAME_FLAG = flag;
	}

	public static final Material DEFAULT_MATERIAL = EnumUtilities.findByAny(Material.class, "GRASS", "GRASS_BLOCK");

	public static final String TYPE_KEY = "Type";
	public static final String MATERIAL_KEY = "Material";
	public static final String MATERIALDATA_KEY = "Data";
	public static final String AMOUNT_KEY = "Amount";
	public static final String NAME_KEY = "Name";
	public static final String LORE_KEY = "Lore";
	public static final String SKIN_KEY = "Skin";
	public static final String SKINURL_KEY = "Skin-URL";
	public static final String GLOW_KEY = "Glow";
	public static final String TOOLTIP_KEY = "Tooltip";
	public static final String COLOR_KEY = "Color";
	public static final String CUSTOMMODELDATA_KEY = "Custom-model-data";
	private static final String PATH_FORMAT = "%s%s";

	private @ToString.Include Material material = DEFAULT_MATERIAL;
	private @ToString.Include Integer materialId = null;
	private @ToString.Include Short materialData = null;
	private @ToString.Include int amount = 1;
	private @ToString.Include String name = null;
	private @ToString.Include List<String> lore = new ArrayList<>();
	private @ToString.Include String skin = null;
	private @ToString.Include Integer customModelData = null;
	private @ToString.Include boolean shouldRemoveModelData = false;
	private @ToString.Include boolean glow = false;
	private @ToString.Include Replacement[] replacements = null;
	private @ToString.Include ColorMode colorMode = ColorMode.COLOR;
	private ItemStack itemToApply = null;
	private @ToString.Include ItemFlag[] flags = DEFAULT_FLAGS;
	private @ToString.Include boolean applyNullValues = false;
	private @ToString.Include boolean cloneOnBuild = true;
	private @ToString.Include boolean forceType = false;
	private @ToString.Include boolean tooltip = true;
	private @ToString.Include RGBColor color = null;
	private @ToString.Include ItemEnchantment[] enchantments = null;
	private @ToString.Include ItemPotionEffect[] potionEffects = null;
	private @ToString.Include @Setter(value = AccessLevel.NONE) Map<String, String> storedData = new HashMap<>();

	public ItemBuilder setMaterial(Material material) {
		this.material = material;
		return this;
	}

	public ItemBuilder setMaterial(String... possibleMaterialNames) {
		Material material = null;

		for (String possibleMaterialName : possibleMaterialNames) {
			try {
				material = Material.valueOf(possibleMaterialName.toUpperCase());
			} catch (IllegalArgumentException e) {
				// Ignore exception
			}
		}

		if (material != null) {
			this.material = material;
		} else {
			PandaPluginClass.getSimpleLogger().sendError("Unable to parse the following options into a material: %s",
					Arrays.asList(possibleMaterialNames).toString());
		}

		return this;
	}

	public ItemBuilder setLore(List<String> lore) {
		this.lore = lore;
		return this;
	}

	public ItemBuilder setLore(String... lore) {
		this.lore = new ArrayList<>();
		Arrays.stream(lore).forEach(this.lore::add);
		return this;
	}

	public ItemBuilder setReplacements(Replacement... replacements) {
		this.replacements = replacements;
		return this;
	}

	public ItemBuilder setEnchantments(ItemEnchantment... enchantments) {
		this.enchantments = enchantments;
		return this;
	}

	public ItemBuilder setPotionEffects(ItemPotionEffect... potionEffects) {
		this.potionEffects = potionEffects;
		return this;
	}

	public ItemBuilder setFlags(ItemFlag... flags) {
		this.flags = flags;
		return this;
	}

	public ItemBuilder setStoredDataValue(String key, String value) {
		this.storedData.put(key, value);
		return this;
	}

	public ItemBuilder removeStoredDataValue(String key) {
		this.storedData.remove(key);
		return this;
	}

	public ItemBuilder fromItem(ItemStack item) {
		if (item != null) {
			this.material = item.getType();
			this.materialData = ItemStackUtilities.GET_DURABILITY_METHOD.isPresent()
					? ItemStackUtilities.GET_DURABILITY_METHOD.invokeSafely(item)
					: null;
			if (this.materialData != null && this.materialData == 0) {
				this.materialData = null;
			}
			this.amount = item.getAmount();
			ItemMeta im = item.hasItemMeta() ? item.getItemMeta() : null;
			if (im != null) {
				this.name = im.hasDisplayName() ? im.getDisplayName() : null;
				this.lore = im.hasLore() ? new ArrayList<>(im.getLore()) : new ArrayList<>();
				this.skin = im instanceof SkullMeta ? SkinUtilities.NMS.getSkinAsBase64(item) : null;
				this.glow = ItemStackUtilities.hasGlowEffect(item);
				this.customModelData = CustomModelDataUtilities.getCustomModelData(im);
				this.tooltip = !im.isHideTooltip();

				if (im instanceof LeatherArmorMeta) {
					this.color = new RGBColor(((LeatherArmorMeta) im).getColor().asRGB());
				}
			}
			this.itemToApply = item.clone();
		} else {
			this.material = null;
			this.amount = 1;
			this.name = null;
			this.lore = new ArrayList<>();
			this.skin = null;
			this.glow = false;
			this.customModelData = null;
			this.tooltip = true;
			this.itemToApply = null;
			this.color = null;
		}
		return this;
	}

	public ItemBuilder fromMap(Map<String, Object> map) {
		return fromMap(map, null);
	}

	public ItemBuilder fromMap(Map<String, Object> map, String prefix) {
		prefix = prefix != null && !prefix.isEmpty() ? prefix + "." : "";

		if (containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, TYPE_KEY))) {
			Object value = getIgnoreCase(map, String.format(PATH_FORMAT, prefix, TYPE_KEY));

			if (value instanceof Number) {
				this.materialId = ((Number) value).intValue();
			} else {
				this.material = Material.valueOf(value.toString().toUpperCase());
			}
		} else if (containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, MATERIAL_KEY))) {
			Object value = getIgnoreCase(map, String.format(PATH_FORMAT, prefix, MATERIAL_KEY));

			if (value instanceof Number) {
				this.materialId = ((Number) value).intValue();
			} else {
				this.material = Material.valueOf(value.toString().toUpperCase());
			}
		}

		this.materialData = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, MATERIALDATA_KEY))
				? (Short) ((Number) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, MATERIALDATA_KEY)))
						.shortValue()
				: this.materialData;
		this.amount = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, AMOUNT_KEY))
				? ((Number) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, AMOUNT_KEY))).intValue()
				: this.amount;
		this.name = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, NAME_KEY))
				? (String) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, NAME_KEY))
				: this.name;
		@SuppressWarnings("unchecked")
		List<String> lore = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, LORE_KEY))
				? (List<String>) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, LORE_KEY))
				: null;
		this.lore = lore != null ? new ArrayList<>(lore) : this.lore;
		this.skin = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, SKIN_KEY))
				? (String) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, SKIN_KEY))
				: this.skin;

		Number number = (Number) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, CUSTOMMODELDATA_KEY));
		this.customModelData = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, CUSTOMMODELDATA_KEY))
				? (number != null ? (Integer) number.intValue() : null)
				: this.customModelData;
		this.glow = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, GLOW_KEY))
				? (Boolean) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, GLOW_KEY))
				: this.glow;
		this.tooltip = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, TOOLTIP_KEY))
				? (Boolean) getIgnoreCase(map, String.format(PATH_FORMAT, prefix, TOOLTIP_KEY))
				: this.tooltip;
		this.color = containsKeyIgnoreCase(map, String.format(PATH_FORMAT, prefix, COLOR_KEY))
				? new RGBColor(getIgnoreCase(map, String.format(PATH_FORMAT, prefix, COLOR_KEY)).toString())
				: this.color;
		return this;
	}

	public Map<String, Object> toMap() {
		return toMap("");
	}

	public Map<String, Object> toMap(String prefix) {
		Map<String, Object> map = new LinkedHashMap<>();

		if (this.material != null) {
			map.put(prefix.concat(TYPE_KEY), this.material.name());
		}

		if (this.materialData != null) {
			map.put(prefix.concat(MATERIALDATA_KEY), this.materialData);
		}

		if (this.amount != 1) {
			map.put(prefix.concat(AMOUNT_KEY), this.amount);
		}

		if (this.name != null) {
			map.put(prefix.concat(NAME_KEY), this.name);
		}

		if (this.lore != null && !this.lore.isEmpty()) {
			map.put(prefix.concat(LORE_KEY), this.lore);
		}

		if (this.skin != null) {
			map.put(prefix.concat(SKIN_KEY), this.skin);
		}

		if (this.customModelData != null || this.shouldRemoveModelData) {
			map.put(prefix.concat(CUSTOMMODELDATA_KEY), this.customModelData);
		}

		if (this.glow) {
			map.put(prefix.concat(GLOW_KEY), this.glow);
		}

		if (this.tooltip) {
			map.put(prefix.concat(TOOLTIP_KEY), this.tooltip);
		}

		if (this.color != null) {
			map.put(prefix.concat(COLOR_KEY), "#" + String.format("%02x", this.color.getRed())
					+ String.format("%02x", this.color.getGreen()) + String.format("%02x", this.color.getBlue()));
		}

		return map;
	}

	public ItemStack build() {
		if (this.material == null || this.amount == 0) {
			return null;
		}

		try {
			if (this.materialData != null && ITEMSTACK_WITH_MATERIAL_DATA_CONTRUCTOR != null) {
				return apply(
						this.itemToApply != null ? (this.cloneOnBuild ? this.itemToApply.clone() : this.itemToApply)
								: (this.itemToApply = ITEMSTACK_WITH_MATERIAL_DATA_CONTRUCTOR.newInstance(this.material,
										this.amount, this.materialData)));
			}
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
				| InvocationTargetException e) {
		}

		return apply(this.itemToApply != null ? (this.cloneOnBuild ? this.itemToApply.clone() : this.itemToApply)
				: (this.itemToApply = new ItemStack(this.material, this.amount)));
	}

	public ItemStack apply(ItemStack item) {
		if (item == null) {
			return null;
		}

		if (this.forceType && item.getType() != this.material) {
			item = new ItemStack(this.material, amount);
		}

		item.setAmount(amount);

		ItemMeta im = item.hasItemMeta() ? item.getItemMeta() : null;

		if (im == null) {
			im = Bukkit.getItemFactory().getItemMeta(item.getType());
		}

		if (this.name != null && !this.name.isEmpty()) {
			switch (colorMode) {
			case IGNORE:
				im.setDisplayName(this.name);
				break;
			default:
				im.setDisplayName(MessageTemplate.inst(this.name).setReplacements(replacements)
						.setColor(colorMode == ColorMode.COLOR).process().toString());
				break;
			}
		} else if (this.applyNullValues) {
			im.setDisplayName(null);
		}

		if (this.lore.size() > 0) {
			switch (colorMode) {
			case IGNORE:
				im.setLore(lore);
				break;
			default:
				im.setLore(MessageTemplate.inst(this.lore).setReplacements(replacements)
						.setColor(colorMode == ColorMode.COLOR).process().getStrings());
				break;
			}
		} else if (this.applyNullValues) {
			im.setLore(null);
		}

		if (this.customModelData != null) {
			CustomModelDataUtilities.setCustomModelData(im, this.customModelData);
		} else if (this.shouldRemoveModelData) {
			CustomModelDataUtilities.setCustomModelData(im, null);
		}

		if (enchantments != null) {
			for (ItemEnchantment enchantment : enchantments) {
				im.addEnchant(enchantment.getEnchantment(), enchantment.getLevel(),
						enchantment.isIgnoreLevelRestriction());
			}
		}

		if (potionEffects != null && im instanceof PotionMeta) {
			for (ItemPotionEffect potionEffect : potionEffects) {
				((PotionMeta) im).addCustomEffect(new PotionEffect(potionEffect.getType(), potionEffect.getDuration(),
						potionEffect.getAmplifier()), false);
			}
		}

		if (color != null && im instanceof LeatherArmorMeta) {
			((LeatherArmorMeta) im).setColor(Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue()));
		}

		im.setHideTooltip(!tooltip);

		im.addItemFlags(flags);

		item.setItemMeta(im);

		if (im instanceof SkullMeta) {
			SkinUtilities.NMS.setSkinSafe(item, this.skin, null);
		}

		if (enchantments == null && ItemStackUtilities.hasGlowEffect(item) != this.glow) {
			ItemStackUtilities.switchGlowEffect(item);
		}

		try {
			ItemStackDataUtilities.setPersistentData(item, PandaPluginClass.getInstance(),
					storedData.entrySet().stream().collect(Collectors.toMap(Entry::getKey, Entry::getValue)));
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return item;
	}

	public ItemBuilder toClone() {
		ItemBuilder builder = ItemBuilder.inst().setAmount(amount).setApplyNullValues(applyNullValues)
				.setCloneOnBuild(cloneOnBuild).setColorMode(colorMode).setCustomModelData(customModelData)
				.setEnchantments(enchantments).setFlags(flags).setForceType(forceType).setGlow(glow)
				.setItemToApply(itemToApply != null ? itemToApply.clone() : null).setLore(new ArrayList<>(lore))
				.setMaterial(material).setMaterialData(materialData).setMaterialId(materialId).setName(name)
				.setPotionEffects(potionEffects).setReplacements(replacements)
				.setShouldRemoveModelData(shouldRemoveModelData).setSkin(skin).setTooltip(tooltip);

		storedData.forEach(builder::setStoredDataValue);

		return builder;
	}

	public static ItemBuilder inst() {
		return new ItemBuilder();
	}

	private static boolean containsKeyIgnoreCase(Map<String, ?> map, String key) {
		return map.keySet().stream().anyMatch(k -> k.equalsIgnoreCase(key));
	}

	private static <T> T getIgnoreCase(Map<String, T> map, String key) {
		return getIgnoreCaseOrDefault(map, key, null);
	}

	private static <T> T getIgnoreCaseOrDefault(Map<String, T> map, String key, T defaultValue) {
		return map.entrySet().stream().filter(entry -> entry.getKey().equalsIgnoreCase(key)).map(Entry::getValue)
				.filter(Objects::nonNull).findFirst().orElse(defaultValue);
	}

	@AllArgsConstructor
	@Data
	public static class ItemEnchantment {

		private Enchantment enchantment;
		private int level = 1;
		private boolean ignoreLevelRestriction = true;

		public ItemEnchantment(Enchantment enchantment) {
			this.enchantment = enchantment;
		}

		public ItemEnchantment(Enchantment enchantment, int level) {
			this.enchantment = enchantment;
			this.level = level;
		}

	}

	@AllArgsConstructor
	@Data
	public static class ItemPotionEffect {

		private PotionEffectType type;
		private int duration = 60 * 20;
		private int amplifier = 1;

		public ItemPotionEffect(PotionEffectType type) {
			this.type = type;
		}

	}

}
