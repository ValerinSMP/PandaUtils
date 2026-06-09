package darkpanda73.PandaUtils.Services.PandaBossBarModule.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.PandaBossBarService;

@PandaListener
public class PandaBossBarListener implements Listener {

	@PandaInject
	private PandaBossBarService bossBarService;

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		bossBarService.getBossBars().forEach(bossBar -> {
			if (bossBar.isGlobal()) {
				bossBar.addPlayer(e.getPlayer());
			}
		});
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerLeave(PlayerQuitEvent e) {
		bossBarService.getBossBars().forEach(bossBar -> bossBar.removePlayer(e.getPlayer()));
	}

}
