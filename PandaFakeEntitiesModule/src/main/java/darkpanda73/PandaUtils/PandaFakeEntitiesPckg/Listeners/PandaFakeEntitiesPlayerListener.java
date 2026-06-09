package darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Listeners;

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

import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.PandaFakeEntitiesService;
import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Objects.PandaFakeEntity;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;

@PandaListener
public class PandaFakeEntitiesPlayerListener implements Listener {

	private static final List<PandaFakeEntity> EMPTY_LIST = new ArrayList<>();

	@PandaInject
	private PandaFakeEntitiesService entitiesService;

	private Map<UUID, List<PandaFakeEntity>> entitiesOnSight = new HashMap<>();

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (entitiesService.isLoaded()) {
			entitiesService.findOnSight(e.getPlayer().getLocation()).forEach(entity -> {
				entity.addViewer(e.getPlayer());
				entitiesOnSight.computeIfAbsent(e.getPlayer().getUniqueId(), (uuid) -> new ArrayList<>()).add(entity);
			});
		}
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e) {
		updateEntities(e.getPlayer(), e.getTo());
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		TasksUtils.execute(() -> updateEntities(e.getPlayer(), e.getTo()));
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		entitiesOnSight.getOrDefault(e.getPlayer().getUniqueId(), EMPTY_LIST)
				.forEach(entity -> entity.removeViewer(e.getPlayer()));
		entitiesOnSight.remove(e.getPlayer().getUniqueId());
	}

	public void resetEntities(Player player, Location location) {
		entitiesOnSight.getOrDefault(player.getUniqueId(), EMPTY_LIST).forEach(entity -> entity.removeViewer(player));
		entitiesOnSight.remove(player.getUniqueId());

		updateEntities(player, location);
	}

	public void updateEntities(Player player, Location location) {
		if (entitiesService.isLoaded()) {
			List<PandaFakeEntity> previousEntitiesOnSight = entitiesOnSight.computeIfAbsent(player.getUniqueId(),
					(uuid) -> new ArrayList<>());
			List<PandaFakeEntity> currentEntitiesOnSight = entitiesService.findOnSight(location);

			previousEntitiesOnSight.removeIf(entity -> {
				if (!currentEntitiesOnSight.contains(entity)) {
					entity.removeViewer(player);
					return true;
				}
				return false;
			});

			currentEntitiesOnSight.forEach(entity -> {
				if (!previousEntitiesOnSight.contains(entity)) {
					entity.addViewer(player);
					previousEntitiesOnSight.add(entity);
				}
			});
		}
	}

}
