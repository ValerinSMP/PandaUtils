package darkpanda73.PandaUtils.PandaHologramsPckg.Listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import darkpanda73.PandaUtils.PandaHologramsPckg.PandaHologramsService;
import darkpanda73.PandaUtils.PandaHologramsPckg.Objects.PandaHologram;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;

@PandaListener
public class PandaHologramPlayerListener implements Listener {

	private static final List<PandaHologram> EMPTY_LIST = new ArrayList<>();

	@PandaInject
	private PandaHologramsService hologramsService;

	private Map<UUID, List<PandaHologram>> hologramsOnSight = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (hologramsService.isLoaded()) {
			hologramsService.findOnSight(e.getPlayer().getLocation()).forEach(hologram -> {
				hologram.addViewer(e.getPlayer());
				hologramsOnSight.computeIfAbsent(e.getPlayer().getUniqueId(), (uuid) -> new ArrayList<>())
						.add(hologram);
			});
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e) {
		updateHolograms(e.getPlayer(), e.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		TasksUtils.execute(() -> updateHolograms(e.getPlayer(), e.getTo()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		hologramsOnSight.getOrDefault(e.getPlayer().getUniqueId(), EMPTY_LIST)
				.forEach(hologram -> hologram.removeViewer(e.getPlayer()));
		hologramsOnSight.remove(e.getPlayer().getUniqueId());
	}

	public void resetHolograms(Player player, Location location) {
		hologramsOnSight.getOrDefault(player.getUniqueId(), EMPTY_LIST)
				.forEach(hologram -> hologram.removeViewer(player));
		hologramsOnSight.remove(player.getUniqueId());

		updateHolograms(player, location);
	}

	public void updateHolograms(Player player, Location location) {
		if (hologramsService.isLoaded()) {
			List<PandaHologram> previousHologramsOnSight = hologramsOnSight.computeIfAbsent(player.getUniqueId(),
					(uuid) -> new ArrayList<>());
			List<PandaHologram> currentHologramsOnSight = hologramsService.findOnSight(location);

			previousHologramsOnSight.removeIf(hologram -> {
				if (!currentHologramsOnSight.contains(hologram)) {
					hologram.removeViewer(player);
					return true;
				}
				return false;
			});

			currentHologramsOnSight.forEach(hologram -> {
				if (!previousHologramsOnSight.contains(hologram)) {
					hologram.addViewer(player);
					previousHologramsOnSight.add(hologram);
				}
			});
		}
	}

}
