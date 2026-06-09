package darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;

@PandaListener
public class PandaCachedPlayersListener implements Listener {

	@PandaInject
	private PandaCachedPlayersService service;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		service.getCachedPlayer(e.getPlayer());
	}

}
