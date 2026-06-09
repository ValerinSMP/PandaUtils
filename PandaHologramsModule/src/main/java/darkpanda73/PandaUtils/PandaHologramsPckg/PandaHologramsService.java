package darkpanda73.PandaUtils.PandaHologramsPckg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import darkpanda73.PandaUtils.PandaHologramsPckg.Listeners.PandaHologramPlayerListener;
import darkpanda73.PandaUtils.PandaHologramsPckg.Objects.PandaHologram;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation.SimpleLocationArea;
import darkpanda73.PandaUtils.PandaUtilities.Objects.QueryContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import lombok.Getter;

@PandaService(priority = -999)
public class PandaHologramsService {

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaHologramPlayerListener playerListener;

	private @Getter boolean loaded = false;

	private HashMap<UUID, PandaHologram> holograms = new HashMap<>();

	private QueryContainer<PandaHologram> queryContainer;

	@LoadMethod
	private void load() throws ReportError {
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			throw new ReportResult.ReportError("ProtocolLib is not available, holograms will be disabled.", false);
		}

		filesService.registerFields(this.getClass());

		queryContainer = new QueryContainer<PandaHologram>(() -> holograms.values(),
				(hologram) -> hologram.getLocation());
		queryContainer.setSearchRadius(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_VIEWDISTANCE.getContent() * 3);
		queryContainer.setValidRadius(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_VIEWDISTANCE.getContent());
		queryContainer.setTtl(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_CACHE_TTL.getContent());

		this.loaded = true;
	}

	@ReloadMethod
	private void reload() throws IOException {
		queryContainer.close();

		queryContainer = new QueryContainer<PandaHologram>(() -> holograms.values(),
				(hologram) -> hologram.getLocation());
		queryContainer.setSearchRadius(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_VIEWDISTANCE.getContent() * 3);
		queryContainer.setValidRadius(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_VIEWDISTANCE.getContent());
		queryContainer.setTtl(PandaHologramVariablesService.SETTINGS_HOLOGRAMS_CACHE_TTL.getContent());

		Bukkit.getOnlinePlayers().forEach(player -> playerListener.resetHolograms(player, player.getLocation()));
	}

	@UnloadMethod
	private void unload() throws IOException {
		holograms.values().forEach(PandaHologram::clear);
		holograms.clear();
		queryContainer.close();
	}

	public List<PandaHologram> getHolograms() {
		return new ArrayList<>(this.holograms.values());
	}

	public List<PandaHologram> findOnSight(Location location) {
		long distance = PandaHologramVariablesService.SETTINGS_HOLOGRAMS_VIEWDISTANCE.getContent();

		return queryContainer
				.query(new SimpleLocationArea(location.getWorld().getName(),
						new SimpleLocation(location.getWorld().getName(), location.getX() - distance,
								location.getY() - distance, location.getZ() - distance),
						new SimpleLocation(location.getWorld().getName(), location.getX() + distance,
								location.getY() + distance, location.getZ() + distance)))
				.getObjects().stream()
				.filter(hologram -> hologram.getLocation().toLocation().distance(location) <= distance)
				.collect(Collectors.toList());
	}

	public void updateHologramOnQueries(PandaHologram hologram) {
		this.queryContainer.onUpdate(hologram);
	}

	public PandaHologram create(Location location) {
		return this.create(SimpleLocation.of(location));
	}

	public PandaHologram create(SimpleLocation location) {
		UUID uuid = UUID.randomUUID();

		while (holograms.containsKey(uuid)) {
			uuid = UUID.randomUUID();
		}

		PandaHologram hologram = new PandaHologram(uuid, location);

		this.holograms.put(uuid, hologram);

		this.queryContainer.onCreate(hologram);

		TasksUtils.execute(() -> {
			Bukkit.getOnlinePlayers().forEach(player -> playerListener.updateHolograms(player, player.getLocation()));
		});

		return hologram;
	}

	public boolean delete(UUID uuid) {
		if (this.holograms.containsKey(uuid)) {
			PandaHologram hologram = this.holograms.remove(uuid);

			hologram.clear();

			this.queryContainer.onDelete(hologram);

			TasksUtils.execute(() -> {
				Bukkit.getOnlinePlayers()
						.forEach(player -> playerListener.updateHolograms(player, player.getLocation()));
			});

			return true;
		}

		return false;
	}

}