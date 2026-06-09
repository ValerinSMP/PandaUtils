package darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Listeners;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.metadata.FixedMetadataValue;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.PandaPlayerDataService;

@PandaListener
public class PandaPlayerDataEvents implements Listener {

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaPlayerDataService<?> playerDataService;

	private HashMap<UUID, Integer> indexes = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
	public void onAsyncPlayerJoin(AsyncPlayerPreLoginEvent e) {
		if (playerDataService != null) {
			if (e.getLoginResult() == Result.ALLOWED) {
				try {
					int index = playerDataService.loadPlayerData(e.getUniqueId());

					if (playerDataService.getPlayerData(index) != null) {
						indexes.put(e.getUniqueId(), index);
					} else {
						e.setLoginResult(Result.KICK_OTHER);
						e.setKickMessage("Unable to retrieve player data. Please, try again.");
					}
				} catch (Exception e1) {
					e1.printStackTrace();

					e.setLoginResult(Result.KICK_OTHER);
					e.setKickMessage("Unable to retrieve player data. Please, try again.");
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerJoinMonitor(AsyncPlayerPreLoginEvent e) {
		if (playerDataService != null) {
			if (e.getLoginResult() != Result.ALLOWED && this.indexes.containsKey(e.getUniqueId())) {
				this.playerDataService.getPlayerDatas().remove(indexes.remove(e.getUniqueId()));
			}
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onAsyncPlayerJoinMonitor(PlayerLoginEvent e) {
		if (playerDataService != null) {
			if (e.getResult() != org.bukkit.event.player.PlayerLoginEvent.Result.ALLOWED
					&& this.indexes.containsKey(e.getPlayer().getUniqueId())) {
				this.playerDataService.getPlayerDatas().remove(indexes.remove(e.getPlayer().getUniqueId()));
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (playerDataService != null) {
			e.getPlayer().setMetadata(PandaPlayerDataService.METADATA_KEY,
					new FixedMetadataValue(plugin, indexes.remove(e.getPlayer().getUniqueId())));
			plugin.sendDebug(getClass(), String.format("&eLoaded player cache data. &8[&a%d/%d&8]",
					playerDataService.getOccupied(), playerDataService.getSize()));
		}
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e) {
		if (playerDataService != null) {
			playerDataService.unloadPlayerData(e.getPlayer());
			plugin.sendDebug(getClass(), String.format("&eRemoving player cache data. &8[&a%d/%d&8]",
					playerDataService.getOccupied(), playerDataService.getSize()));
		}
	}

}
