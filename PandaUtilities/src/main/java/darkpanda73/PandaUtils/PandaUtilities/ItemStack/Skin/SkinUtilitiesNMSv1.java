package darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.UUID;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;

import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldFinder;
import darkpanda73.PandaUtils.Utilities.Java.Fields.FieldFinder.FieldResult;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class SkinUtilitiesNMSv1 extends AbstractSkinNMSUtilities {

	public static boolean initialized = false;
	public static MethodResult SKULLMETA_SET_PROFILE_METHOD;
	public static MethodResult SKULLMETA_GET_PROFILE_METHOD;
	public static FieldResult SKULLMETA_PROFILE_FIELD;

	public static MethodResult PROPERTY_VALUE_METHOD;
	public static MethodResult PROPERTY_GET_VALUE_METHOD;

	public synchronized void initialize(Class<? extends SkullMeta> skullMeta) {
		SKULLMETA_SET_PROFILE_METHOD = MethodFinder.forClass(skullMeta).setName("setProfile")
				.setParams(GameProfile.class).findSafely();
		SKULLMETA_GET_PROFILE_METHOD = MethodFinder.forClass(skullMeta).setName("getProfile").findSafely();
		SKULLMETA_PROFILE_FIELD = FieldFinder.forClass(skullMeta).setName("profile").findSafely();

		PROPERTY_VALUE_METHOD = MethodFinder.forClass(Property.class).setName("value").findSafely();
		PROPERTY_GET_VALUE_METHOD = MethodFinder.forClass(Property.class).setName("getValue").findSafely();

		initialized = true;
	}

	@Override
	public void setSkinByBase64(ItemStack item, String base64) throws Exception {
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		if (!initialized) {
			initialize(meta.getClass());
		}

		try {
			GameProfile profile = null;

			if (base64 != null && !base64.isEmpty()) {
				profile = new GameProfile(UUID.randomUUID(), "0");
				profile.getProperties().put("textures", new Property("textures", base64, ""));
			}

			if (SKULLMETA_SET_PROFILE_METHOD != null) {
				try {
					SKULLMETA_SET_PROFILE_METHOD.invoke(meta, profile);
				} catch (Exception e) {
					SKULLMETA_PROFILE_FIELD.setValue(meta, profile);
				}
			} else {
				SKULLMETA_PROFILE_FIELD.setValue(meta, profile);
			}

			item.setItemMeta(meta);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void setSkinByUrl(ItemStack item, String url) throws Exception {
		throw new IllegalStateException("Skin URL method is not currently available");
	}

	@Override
	public String getSkinAsBase64(ItemStack item) {
		SkullMeta meta = (SkullMeta) item.getItemMeta();

		if (!initialized) {
			initialize(meta.getClass());
		}

		GameProfile profile = null;

		if (SKULLMETA_GET_PROFILE_METHOD != null) {
			try {
				profile = (GameProfile) SKULLMETA_GET_PROFILE_METHOD.invoke(item.getItemMeta());
			} catch (Exception e) {
				try {
					profile = (GameProfile) SKULLMETA_PROFILE_FIELD.getValue(item.getItemMeta());
				} catch (SecurityException | IllegalArgumentException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			}
		} else {
			try {
				profile = (GameProfile) SKULLMETA_PROFILE_FIELD.getValue(item.getItemMeta());
			} catch (SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}

		if (profile != null) {
			Collection<Property> properties = profile.getProperties().get("textures");
			if (!properties.isEmpty()) {
				try {
					return (String) (PROPERTY_VALUE_METHOD != null ? PROPERTY_VALUE_METHOD : PROPERTY_GET_VALUE_METHOD)
							.invoke(properties.iterator().next());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}

		return null;
	}

	@Override
	public String getSkinAsURL(ItemStack item) {
		return null;
	}

}
