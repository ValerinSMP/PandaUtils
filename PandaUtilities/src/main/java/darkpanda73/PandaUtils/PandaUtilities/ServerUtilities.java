package darkpanda73.PandaUtils.PandaUtilities;

import org.bukkit.Bukkit;

public class ServerUtilities {

	public static final String VERSION;

	static {
		String version = null;
		try {
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (Throwable e) {
			System.out.println("Unable to retrieve the NMS version. Assuming it's using the new package nomenclature.");
		}
		VERSION = version;
	}

}
