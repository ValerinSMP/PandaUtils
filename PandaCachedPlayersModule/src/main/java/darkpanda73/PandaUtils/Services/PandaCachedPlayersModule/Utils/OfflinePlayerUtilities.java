package darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Utils;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

public class OfflinePlayerUtilities {

	public static OfflinePlayer getOfflinePlayer(UUID playerUuid) {
		return Bukkit.getOfflinePlayer(playerUuid);
	}

	public static OfflinePlayer getOfflinePlayer(String playerName) {
		OfflinePlayer pl = Bukkit.getPlayer(playerName);
		if (pl != null) {
			return pl;
		}
		return Arrays.stream(Bukkit.getOfflinePlayers())
				.filter(op -> op.getName() != null && playerName.equalsIgnoreCase(op.getName())).findFirst()
				.orElse(null);
	}

}
