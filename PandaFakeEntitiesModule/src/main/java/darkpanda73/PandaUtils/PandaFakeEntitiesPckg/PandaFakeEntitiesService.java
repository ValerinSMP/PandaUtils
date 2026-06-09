package darkpanda73.PandaUtils.PandaFakeEntitiesPckg;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Listeners.PandaFakeEntitiesPlayerListener;
import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Objects.PandaFakeEntity;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation.SimpleLocationArea;
import darkpanda73.PandaUtils.PandaUtilities.Objects.QueryContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import lombok.Getter;

@PandaService(priority = -999)
public class PandaFakeEntitiesService {

	@PandaInject
	private PandaFilesService filesService;

	@PandaInject
	private PandaFakeEntitiesPlayerListener playerListener;

	private @Getter boolean loaded = false;

	private HashMap<UUID, PandaFakeEntity> entities = new HashMap<>();

	private QueryContainer<PandaFakeEntity> queryContainer;

	@LoadMethod
	private void load() throws ReportError {
		if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
			throw new ReportResult.ReportError("ProtocolLib is not available, fake entities will be disabled.", false);
		}

		InjectUtils.inject(PandaFakeEntity.class, this);

		filesService.registerFields(this.getClass());

		queryContainer = new QueryContainer<PandaFakeEntity>(() -> entities.values(), (entity) -> entity.getLocation());
		queryContainer
				.setSearchRadius(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_VIEWDISTANCE.getContent() * 3);
		queryContainer
				.setValidRadius(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_VIEWDISTANCE.getContent());
		queryContainer.setTtl(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_CACHE_TTL.getContent());

		this.loaded = true;
	}

	@ReloadMethod
	private void reload() throws IOException {
		queryContainer.close();

		queryContainer = new QueryContainer<PandaFakeEntity>(() -> entities.values(), (entity) -> entity.getLocation());
		queryContainer
				.setSearchRadius(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_VIEWDISTANCE.getContent() * 3);
		queryContainer
				.setValidRadius(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_VIEWDISTANCE.getContent());
		queryContainer.setTtl(PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_CACHE_TTL.getContent());

		Bukkit.getOnlinePlayers().forEach(player -> playerListener.resetEntities(player, player.getLocation()));
	}

	@UnloadMethod
	private void unload() throws IOException {
		entities.values().forEach(PandaFakeEntity::clear);
		entities.clear();
		queryContainer.close();
	}

	public PandaFakeEntity getEntity(UUID uuid) {
		return this.entities.get(uuid);
	}

	public List<PandaFakeEntity> getEntities() {
		return new ArrayList<>(this.entities.values());
	}

	public List<PandaFakeEntity> findOnSight(Location location) {
		long distance = PandaFakeEntitiesVariablesService.SETTINGS_FAKEENTITIES_VIEWDISTANCE.getContent();

		return queryContainer
				.query(new SimpleLocationArea(location.getWorld().getName(),
						new SimpleLocation(location.getWorld().getName(), location.getX() - distance,
								location.getY() - distance, location.getZ() - distance),
						new SimpleLocation(location.getWorld().getName(), location.getX() + distance,
								location.getY() + distance, location.getZ() + distance)))
				.getObjects().stream()
				.filter(entity -> entity.getLocation().toLocation().distance(location) <= distance)
				.collect(Collectors.toList());
	}

	public void updateEntityOnQueries(PandaFakeEntity entity) {
		this.queryContainer.onUpdate(entity);
	}

	public <T extends PandaFakeEntity> T register(T entity) {
		if (entity != null) {
			this.entities.put(entity.getUuid(), entity);

			this.queryContainer.onCreate(entity);

			TasksUtils.execute(() -> {
				Bukkit.getOnlinePlayers()
						.forEach(player -> playerListener.updateEntities(player, player.getLocation()));
			});

			return entity;
		} else {
			throw new NullPointerException("Unable to parse entity type to fake entity");
		}
	}

	public boolean delete(UUID uuid) {
		if (this.entities.containsKey(uuid)) {
			PandaFakeEntity entity = this.entities.remove(uuid);

			entity.clear();

			this.queryContainer.onDelete(entity);

			TasksUtils.execute(() -> {
				Bukkit.getOnlinePlayers()
						.forEach(player -> playerListener.updateEntities(player, player.getLocation()));
			});

			return true;
		}

		return false;
	}

}