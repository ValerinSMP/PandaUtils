package darkpanda73.PandaUtils.PandaUtilities.Blocks.NMS;

import java.net.URI;
import java.net.URL;
import java.util.Base64;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Skull;

import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONElement;
import darkpanda73.PandaUtils.PandaJSON.Utils.PandaJSONUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import lombok.AccessLevel;
import lombok.Getter;

@Getter(value = AccessLevel.PROTECTED)
public class BlockNMSv3 extends BlockNMSAbstract {

	private Class<?> playerProfileClass;
	private Class<?> playerTexturesClass;

	private MethodResult createPlayerProfileMethod;

	private MethodResult playerProfileGetTexturesMethod;
	private MethodResult playerProfileSetTexturesMethod;

	private MethodResult playerTexturesSetSkinMethod;

	private MethodResult skullSetOwnerProfileMethod;
	private MethodResult skullGetOwnerProfileMethod;

	public BlockNMSv3() throws ClassNotFoundException {
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

			skullSetOwnerProfileMethod = MethodFinder.forData(Skull.class, "setOwnerProfile", playerProfileClass)
					.find();
		} catch (Exception e) {
			throw e;
		}
		playerProfileClass = Class.forName("org.bukkit.profile.PlayerProfile");
		playerTexturesClass = Class.forName("org.bukkit.profile.PlayerTextures");

		createPlayerProfileMethod = MethodFinder
				.forData(Bukkit.getServer().getClass(), "createPlayerProfile", String.class).find();
		playerProfileGetTexturesMethod = MethodFinder.forData(playerProfileClass, "getTextures")
				.setReturnType(playerTexturesClass).find();
		playerProfileSetTexturesMethod = MethodFinder.forData(playerProfileClass, "setTextures", playerTexturesClass)
				.find();
		playerTexturesSetSkinMethod = MethodFinder.forData(playerTexturesClass, "setSkin", URL.class).find();
		skullSetOwnerProfileMethod = MethodFinder.forData(Skull.class, "setOwnerProfile", playerProfileClass).find();
	}

	@Override
	public void setSkin(Block block, String base64) {
		if (base64 != null && !base64.isEmpty()) {
			try {
				PandaJSONElement obj = PandaJSONUtilities.parseJson(new String(Base64.getDecoder().decode(base64)));

				if (obj.isObject()) {
					Object profile = createPlayerProfileMethod.invoke(Bukkit.getServer(), "0");
					Object textures = playerProfileGetTexturesMethod.invoke(profile);

					playerTexturesSetSkinMethod.invoke(textures, URI
							.create(obj.asObject().getObject("textures").getObject("SKIN").getString("url")).toURL());
					playerProfileSetTexturesMethod.invoke(profile, textures);

					Skull skullMeta = (Skull) block.getState();
					skullSetOwnerProfileMethod.invoke(skullMeta, profile);
					skullMeta.update();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				Skull skullMeta = (Skull) block.getState();
				skullSetOwnerProfileMethod.invoke(skullMeta, (Object) null);
				skullMeta.update();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
