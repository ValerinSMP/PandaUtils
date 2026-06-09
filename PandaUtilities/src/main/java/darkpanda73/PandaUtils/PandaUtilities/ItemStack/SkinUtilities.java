package darkpanda73.PandaUtils.PandaUtilities.ItemStack;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin.AbstractSkinNMSUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin.SkinUtilitiesNMSv1;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.Skin.SkinUtilitiesNMSv2;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassCreator;

public class SkinUtilities {

	public static final AbstractSkinNMSUtilities NMS;

	static {
		AbstractSkinNMSUtilities nms;
		try {
			nms = ClassCreator.<AbstractSkinNMSUtilities>inst()
					.setClasses(SkinUtilitiesNMSv2.class, SkinUtilitiesNMSv1.class).getInstance();
		} catch (Throwable e) {
			e.printStackTrace();
			nms = null;
		}
		NMS = nms;
	}

}
