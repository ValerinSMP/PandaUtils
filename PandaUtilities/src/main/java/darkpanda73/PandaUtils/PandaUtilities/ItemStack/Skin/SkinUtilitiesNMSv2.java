package darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin;

import java.net.URI;
import java.net.URL;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONElement;
import darkpanda73.PandaUtils.PandaJSON.Utils.PandaJSONUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class SkinUtilitiesNMSv2 extends AbstractSkinNMSUtilities {

	private static final String TEXTURES_TEMPLATE = "{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";

	private Class<?> playerProfileClass;
	private Class<?> playerTexturesClass;

	private MethodResult createPlayerProfileMethod;

	private MethodResult playerProfileGetTexturesMethod;
	private MethodResult playerProfileSetTexturesMethod;

	private MethodResult playerTexturesSetSkinMethod;
	private MethodResult playerTexturesGetSkinMethod;

	private MethodResult skullMetaSetOwnerProfileMethod;
	private MethodResult skullMetaGetOwnerProfileMethod;

	public SkinUtilitiesNMSv2() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		try {
			playerProfileClass = ClassFinder.forClass("org.bukkit.profile.PlayerProfile").find().getFoundClass();
			playerTexturesClass = ClassFinder.forClass("org.bukkit.profile.PlayerTextures").find().getFoundClass();

			createPlayerProfileMethod = MethodFinder
					.forData(Bukkit.getServer().getClass(), "createPlayerProfile", String.class).find();

			playerProfileGetTexturesMethod = MethodFinder.forData(playerProfileClass, "getTextures")
					.setReturnType(playerTexturesClass).find();
			playerProfileSetTexturesMethod = MethodFinder
					.forData(playerProfileClass, "setTextures", playerTexturesClass).find();

			playerTexturesSetSkinMethod = MethodFinder.forData(playerTexturesClass, "setSkin", URL.class).find();
			playerTexturesGetSkinMethod = MethodFinder.forData(playerTexturesClass, "getSkin").find();

			skullMetaSetOwnerProfileMethod = MethodFinder
					.forData(SkullMeta.class, "setOwnerProfile", playerProfileClass).find();
			skullMetaGetOwnerProfileMethod = MethodFinder.forData(SkullMeta.class, "getOwnerProfile").find();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void setSkinByBase64(ItemStack item, String base64) throws Exception {
		PandaJSONElement obj = PandaJSONUtilities.parseJson(new String(Base64.getDecoder().decode(base64)));

		if (obj.isObject()) {
			setSkinByUrl(item, obj.asObject().getObject("textures").getObject("SKIN").getString("url"));
		}
	}

	@Override
	public void setSkinByUrl(ItemStack item, String url) throws Exception {
		Object profile = createPlayerProfileMethod.invoke(Bukkit.getServer(), "0");
		Object textures = playerProfileGetTexturesMethod.invoke(profile);

		playerTexturesSetSkinMethod.invoke(textures, URI.create(url).toURL());
		playerProfileSetTexturesMethod.invoke(profile, textures);

		SkullMeta skullMeta = (SkullMeta) item.getItemMeta();
		skullMetaSetOwnerProfileMethod.invoke(skullMeta, profile);
		item.setItemMeta(skullMeta);
	}

	@Override
	public String getSkinAsBase64(ItemStack item) {
		String skinUrl = getSkinAsURL(item);
		return skinUrl != null
				? Base64.getEncoder().encodeToString(String.format(TEXTURES_TEMPLATE, skinUrl).getBytes())
				: null;
	}

	@Override
	public String getSkinAsURL(ItemStack item) {
		if (item.hasItemMeta()) {
			Object playerProfile = skullMetaGetOwnerProfileMethod.invokeSafely(item.getItemMeta());
			if (playerProfile != null) {
				Object textures = playerProfileGetTexturesMethod.invokeSafely(playerProfile);
				if (textures != null) {
					URL url = playerTexturesGetSkinMethod.invokeSafely(textures);
					if (url != null) {
						return url.toString();
					}
				}
			}
		}
		return null;
	}

}
