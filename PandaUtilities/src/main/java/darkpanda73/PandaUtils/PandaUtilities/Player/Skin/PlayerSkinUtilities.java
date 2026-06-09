package darkpanda73.PandaUtils.PandaUtilities.Player.Skin;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassCreator;

public class PlayerSkinUtilities {

	public static final AbstractPlayerSkinNMSUtilities NMS;

	static {
		AbstractPlayerSkinNMSUtilities nms;
		try {
			nms = ClassCreator.<AbstractPlayerSkinNMSUtilities>inst().setClasses(PlayerSkinUtilitiesNMSv1.class)
					.getInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			nms = null;
		}
		NMS = nms;
	}

	public static String getSkinAsBase64(Player player) {
		return NMS.getSkinAsBase64(player);
	}

	public static String getSkinAsBase64Safely(Player player) {
		try {
			return getSkinAsBase64(player);
		} catch (Throwable e) {
			return null;
		}
	}

	public static String getSkinAsURL(Player player) {
		return NMS.getSkinAsURL(player);
	}

	public static String getSkinAsURLSafely(Player player) {
		try {
			return getSkinAsURL(player);
		} catch (Throwable e) {
			return null;
		}
	}

}
