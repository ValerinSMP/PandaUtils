package darkpanda73.PandaUtils.PandaUtilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerUtilities {

	public static OfflinePlayer getOfflinePlayer(UUID playerUuid) {
		return Bukkit.getOfflinePlayer(playerUuid);
	}

	public static OfflinePlayer getOfflinePlayer(String playerName) {
		OfflinePlayer pl = Bukkit.getPlayerExact(playerName);
		if (pl != null) {
			return pl;
		}
		return Bukkit.getOfflinePlayerIfCached(playerName);
	}

}
