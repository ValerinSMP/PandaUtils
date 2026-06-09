package darkpanda73.PandaUtils.PandaUtilities;

import java.lang.reflect.Method;

import org.bukkit.World;

public class WorldUtilities {

	public static final Method getMinHeight;

	static {
		Method fGetMinHeight;
		try {
			fGetMinHeight = org.bukkit.World.class.getMethod("getMinHeight");
		} catch (Exception e) {
			fGetMinHeight = null;
		}
		getMinHeight = fGetMinHeight;
	}

	public static int getMinHeight(World world) {
		int height = 0;
		if (getMinHeight != null) {
			try {
				height = (int) getMinHeight.invoke(world);
			} catch (Exception e) {
			}
		}
		return height;
	}

}
