package darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.PandaFakeEntitiesService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import darkpanda73.PandaUtils.PandaUtilities.Objects.QueryContainer.IdentifiedEntity;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;
import lombok.ToString;

@ToString
public abstract class PandaFakeEntity implements IdentifiedEntity {

	@PandaInject
	private static PandaFakeEntitiesService entitiesService;

	private @Getter UUID uuid;
	private @Getter SimpleLocation location;

	private Pair<UUID, Integer> entity = null;

	private List<UUID> viewers = new ArrayList<>();

	public PandaFakeEntity(SimpleLocation location) {
		this.location = location;
	}

	public void register() {
		this.uuid = UUID.randomUUID();

		while (entitiesService.getEntity(uuid) != null) {
			uuid = UUID.randomUUID();
		}

		entitiesService.register(this);
	}

	public void delete() {
		entitiesService.delete(uuid);
	}

	public void setLocation(SimpleLocation location) {
		this.location = location;

		List<Player> onlineViewers = Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList());

		if (!onlineViewers.isEmpty()) {
			this.teleportEntity(location, onlineViewers);
		}

		entitiesService.updateEntityOnQueries(this);
	}

	public void addViewer(Player player) {
		this.viewers.add(player.getUniqueId());

		this.spawnEntity(this.location, Collections.singletonList(player));
	}

	public void removeViewer(Player player) {
		this.removeViewer(player.getUniqueId());

		this.removeEntity(Collections.singletonList(player));
	}

	public void removeViewer(UUID uuid) {
		this.viewers.remove(uuid);
	}

	public boolean isViewer(Player player) {
		return isViewer(player.getUniqueId());
	}

	public boolean isViewer(UUID uuid) {
		return this.viewers.contains(uuid);
	}

	public void removeViewers() {
		this.removeEntity(Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList()));
		this.viewers.clear();
	}

	public void clear() {
		removeViewers();
		this.entity = null;
	}

	public abstract EntityType getEntityType();

	protected Pair<UUID, Integer> getEntityId() {
		if (this.entity == null) {
			UUID uuid = UUID.randomUUID();

			Location worldCenter = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
			Entity entity = worldCenter.getWorld().spawnEntity(worldCenter, EntityType.ARMOR_STAND);
			entity.remove();

			return this.entity = Pair.of(uuid, entity.getEntityId());
		}
		return this.entity;
	}

	protected void spawnEntity(SimpleLocation location, List<Player> players) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

		packet.getIntegers().write(0, this.getEntityId().getSecond());
		packet.getUUIDs().write(0, this.getEntityId().getFirst());
		packet.getEntityTypeModifier().write(0, getEntityType());

		packet.getDoubles().write(0, location.getX()).write(1, location.getY()).write(2, location.getZ());

		if (packet.getVectors().getFields().size() > 0) {
			packet.getVectors().write(0, new Vector(0, 0, 0));
		}

		players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));
	}

	protected void teleportEntity(SimpleLocation newLocation, List<Player> players) {
		if (this.getEntityId() != null) {
			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

			packet.getIntegers().write(0, this.getEntityId().getSecond());
			packet.getDoubles().write(0, newLocation.getX());
			packet.getDoubles().write(1, newLocation.getY());
			packet.getDoubles().write(2, newLocation.getZ());
			packet.getDoubles().write(3, 0D);
			packet.getDoubles().write(4, 0D);
			packet.getDoubles().write(5, 0D);
			packet.getFloat().write(0, newLocation.getYaw());
			packet.getFloat().write(1, newLocation.getPitch());
			packet.getBooleans().write(0, true);

			players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));
		}
	}

	private void removeEntity(List<Player> players) {
		if (this.getEntityId() != null) {
			PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

			packet.getIntLists().write(0, Arrays.asList(this.getEntityId().getSecond()));

			players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));
		}
	}

}
