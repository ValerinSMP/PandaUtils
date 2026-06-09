package darkpanda73.PandaUtils.PandaUtilities.Entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Ageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.MagmaCube;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;

import lombok.Data;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class EntityBuilder {

	private static final String GENERIC_SCALE_ATTRIBUTE = "GENERIC_SCALE";

	private EntityType entityType;

	/* Common fields */

	private Double customHealth;
	private Double customDamage;
	private Double customSpeed;
	private Double customSize;

	private ItemStack weapon;

	private ItemStack helmet;
	private ItemStack chestplate;
	private ItemStack leggings;
	private ItemStack boots;

	private @Setter(lombok.AccessLevel.NONE) Map<Attribute, List<AttributeModifier>> attributeModifiers = new HashMap<>();
	private @Setter(lombok.AccessLevel.NONE) List<EntityBuilder> passengers = new ArrayList<>();

	/* Ageable entities fields */

	private boolean child = false;

	/* Slime fields */

	private int slimeSize = 2;

	public EntityBuilder(EntityType type) {
		this.entityType = type;
	}

	public EntityBuilder addPassenger(EntityBuilder builder) {
		this.passengers.add(builder);
		return this;
	}

	public EntityBuilder copy(EntityBuilder originalBuilder) {
		this.entityType = originalBuilder.getEntityType();

		this.customHealth = originalBuilder.getCustomHealth();
		this.customDamage = originalBuilder.getCustomDamage();
		this.customSpeed = originalBuilder.getCustomSpeed();
		this.customSize = originalBuilder.getCustomSize();

		this.weapon = originalBuilder.getWeapon();

		this.helmet = originalBuilder.getHelmet();
		this.chestplate = originalBuilder.getChestplate();
		this.leggings = originalBuilder.getLeggings();
		this.boots = originalBuilder.getBoots();

		return this;
	}

	@SuppressWarnings("deprecation")
	public List<Entity> spawn(Location location) {
		List<Entity> entities = new ArrayList<>();

		Entity entity = location.getWorld().spawnEntity(location, entityType);

		entities.add(entity);

		if (entity.isInsideVehicle()) {
			Entity vehicle = entity.getVehicle();
			entity.leaveVehicle();
			vehicle.remove();
		}

		if (entity instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity) entity;

			livingEntity.getEquipment().clear();
			livingEntity.getEquipment().setItemInMainHand(weapon);
			livingEntity.getEquipment().setHelmet(helmet, true);
			livingEntity.getEquipment().setChestplate(chestplate, true);
			livingEntity.getEquipment().setLeggings(leggings, true);
			livingEntity.getEquipment().setBoots(boots, true);

			if (customHealth != null && livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
				livingEntity.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(customHealth);
			}

			if (customDamage != null && livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE) != null) {
				livingEntity.getAttribute(Attribute.GENERIC_ATTACK_DAMAGE).setBaseValue(customDamage);
			}

			if (customSpeed != null && livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED) != null) {
				livingEntity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(customSpeed);
			}

			try {
				Attribute scaleAttribute = Attribute.valueOf(GENERIC_SCALE_ATTRIBUTE);
				if (customSize != null && livingEntity.getAttribute(scaleAttribute) != null) {
					livingEntity.getAttribute(scaleAttribute).setBaseValue(customSize);
				}
			} catch (IllegalArgumentException e) {
				// Ignore cause the scale attribute could not be found.
			}

			this.attributeModifiers.forEach((attribute, modifiers) -> {
				AttributeInstance instance = livingEntity.getAttribute(attribute);
				if (instance != null) {
					modifiers.forEach(modifier -> instance.addModifier(modifier));
				}
			});
		}

		if (entity instanceof Ageable) {
			Ageable ageable = (Ageable) entity;

			if (!child) {
				ageable.setAdult();
			} else {
				ageable.setBaby();
			}

			ageable.setAgeLock(true);
		}

		if (entity instanceof Slime) {
			((Slime) entity).setSize(slimeSize);
		} else if (entity instanceof MagmaCube) {
			((MagmaCube) entity).setSize(slimeSize);
		}

		this.passengers.forEach(builder -> {
			List<Entity> passengerEntities = builder.spawn(location);
			entity.addPassenger(passengerEntities.get(0));
			entities.addAll(passengerEntities);
		});

		return entities;
	}

	public long getExpectedAmountOfEntities() {
		return 1 + passengers.stream().collect(Collectors.summingLong(EntityBuilder::getExpectedAmountOfEntities));
	}

	public static EntityBuilder forEntityType(EntityType type) {
		return new EntityBuilder(type);
	}

}
