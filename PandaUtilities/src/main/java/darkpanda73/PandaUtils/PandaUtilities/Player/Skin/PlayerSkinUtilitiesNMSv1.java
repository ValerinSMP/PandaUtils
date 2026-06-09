package darkpanda73.PandaUtils.PandaUtilities.Player.Skin;

import java.net.URL;
import java.util.Base64;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class PlayerSkinUtilitiesNMSv1 extends AbstractPlayerSkinNMSUtilities {

	private static final String TEXTURES_TEMPLATE = "{\"textures\":{\"SKIN\":{\"url\":\"%s\"}}}";

	private Class<?> playerProfileClass;
	private Class<?> playerTexturesClass;

	private MethodResult playerGetPlayerProfileMethod;

	private MethodResult playerProfileGetTexturesMethod;

	private MethodResult playerTexturesGetSkinMethod;

	public PlayerSkinUtilitiesNMSv1() throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		try {
			playerProfileClass = ClassFinder.forClass("org.bukkit.profile.PlayerProfile").find().getFoundClass();
			playerTexturesClass = ClassFinder.forClass("org.bukkit.profile.PlayerTextures").find().getFoundClass();

			playerProfileGetTexturesMethod = MethodFinder.forData(playerProfileClass, "getTextures")
					.setReturnType(playerTexturesClass).find();

			playerTexturesGetSkinMethod = MethodFinder.forData(playerTexturesClass, "getSkin").find();

			playerGetPlayerProfileMethod = MethodFinder.forData(OfflinePlayer.class, "getPlayerProfile")
					.setReturnType(playerProfileClass).find();
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public String getSkinAsBase64(Player player) {
		String skinUrl = getSkinAsURL(player);
		return skinUrl != null
				? Base64.getEncoder().encodeToString(String.format(TEXTURES_TEMPLATE, skinUrl).getBytes())
				: null;
	}

	@Override
	public String getSkinAsURL(Player player) {
		if (player != null) {
			Object playerProfile = playerGetPlayerProfileMethod.invokeSafely(player);
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
