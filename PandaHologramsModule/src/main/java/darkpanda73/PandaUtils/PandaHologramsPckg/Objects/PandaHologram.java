package darkpanda73.PandaUtils.PandaHologramsPckg.Objects;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedDataValue;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Registry;
import com.comphenix.protocol.wrappers.WrappedDataWatcher.Serializer;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaHologramsPckg.PandaHologramsService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import darkpanda73.PandaUtils.PandaUtilities.Objects.QueryContainer.IdentifiedEntity;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@ToString
@RequiredArgsConstructor
public class PandaHologram implements IdentifiedEntity {

	@PandaInject
	private static PandaHologramsService hologramsService;

	private @Getter UUID uuid;
	private @Getter SimpleLocation location;

	private double lineSpacing = 0.3D;

	private List<String> lines = new ArrayList<>();
	private List<String> rawLines = new ArrayList<>();
	private List<Pair<UUID, Integer>> entities = new ArrayList<>();

	private List<UUID> viewers = new ArrayList<>();

	public PandaHologram(UUID uuid, SimpleLocation location) {
		this.uuid = uuid;
		this.location = location;
	}

	public void setLocation(SimpleLocation location) {
		this.location = location;

		List<Player> onlineViewers = Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList());

		if (!onlineViewers.isEmpty()) {
			this.entities.forEach(entity -> this.teleportEntity(entity.getSecond(), location, onlineViewers));
		}

		hologramsService.updateHologramOnQueries(this);
	}

	public void addLines(List<String> lines) {
		lines.forEach(line -> insertLine(line, this.lines.size()));
	}

	public void addLine(String line) {
		insertLine(line, this.lines.size());
	}

	public void insertLine(String line, int index) {
		this.lines.add(index, MessageTemplate.inst(line).toString());
		this.rawLines.add(index, line);

		List<Player> onlineViewers = Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList());

		Pair<UUID, Integer> entity = null;

		if (line != null && !line.isEmpty()) {
			entity = generateEntity();
		}

		this.entities.add(index, entity);

		if (!onlineViewers.isEmpty()) {
			if (entity != null) {
				SimpleLocation lineLocation = this.location.clone().add(0D,
						this.lineSpacing * (double) (lines.size() - index - 1), 0D);

				spawnEntity(entity.getFirst(), entity.getSecond(), lineLocation, this.lines.get(index), onlineViewers);
			}

			for (int otherIndex = index - 1; otherIndex >= 0; otherIndex--) {
				Pair<UUID, Integer> otherEntity = this.entities.get(otherIndex);

				if (otherEntity != null) {
					this.teleportEntity(otherEntity.getSecond(), this.location.clone().add(0D,
							this.lineSpacing * (double) (lines.size() - otherIndex - 1), 0D), onlineViewers);
				}
			}
		}
	}

	public void setLine(String line, int index) {
		this.lines.set(index, MessageTemplate.inst(line).toString());
		this.rawLines.set(index, line);

		List<Player> onlineViewers = Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList());

		Pair<UUID, Integer> oldEntity = this.entities.get(index);
		Pair<UUID, Integer> newEntity = oldEntity;

		if (line != null && !line.isEmpty()) {
			if (oldEntity == null) {
				this.entities.set(index, newEntity = generateEntity());
			}
		} else if (oldEntity != null) {
			this.entities.set(index, newEntity = null);
		}

		if (!onlineViewers.isEmpty()) {
			if (oldEntity != newEntity) {
				if (newEntity != null) {
					spawnEntity(newEntity.getFirst(), newEntity.getSecond(),
							this.location.clone().add(0D, this.lineSpacing * (double) (lines.size() - index - 1), 0D),
							this.lines.get(index), onlineViewers);
				} else {
					removeEntities(Collections.singletonList(oldEntity.getSecond()), onlineViewers);
				}
			} else if (oldEntity != null) {
				renameEntity(oldEntity.getSecond(), this.lines.get(index), onlineViewers);
			}
		}
	}

	public void removeLine(int index) {
		this.lines.remove(index);
		this.rawLines.remove(index);
		Pair<UUID, Integer> entity = this.entities.remove(index);

		List<Player> onlineViewers = Bukkit.getOnlinePlayers().stream()
				.filter(player -> this.viewers.contains(player.getUniqueId())).collect(Collectors.toList());

		if (!onlineViewers.isEmpty()) {
			removeEntities(Collections.singletonList(entity.getSecond()), onlineViewers);

			for (int otherIndex = index; otherIndex >= 0; otherIndex--) {
				String rawLine = this.rawLines.get(otherIndex);

				if (rawLine != null && !rawLine.isEmpty()) {
					Pair<UUID, Integer> otherEntity = this.entities.get(otherIndex);

					this.teleportEntity(otherEntity.getSecond(), this.location.clone().add(0D,
							this.lineSpacing * (double) (lines.size() - otherIndex - 1), 0D), onlineViewers);
				}
			}
		}
	}

	public void addViewer(Player player) {
		this.viewers.add(player.getUniqueId());

		for (int i = 0; i < this.lines.size() && i < this.entities.size(); i++) {
			Pair<UUID, Integer> entity = this.entities.get(i);

			if (entity != null) {
				this.spawnEntity(entity.getFirst(), entity.getSecond(),
						this.location.clone().add(0D, this.lineSpacing * (double) (double) (lines.size() - i - 1), 0D),
						this.lines.get(i), Collections.singletonList(player));
			}
		}
	}

	public void removeViewer(Player player) {
		this.removeViewer(player.getUniqueId());

		this.removeEntities(
				this.entities.stream().filter(Objects::nonNull).map(Pair::getSecond).collect(Collectors.toList()),
				Collections.singletonList(player));
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
		this.removeEntities(
				this.entities.stream().filter(Objects::nonNull).map(Pair::getSecond).collect(Collectors.toList()),
				Bukkit.getOnlinePlayers().stream().filter(player -> this.viewers.contains(player.getUniqueId()))
						.collect(Collectors.toList()));
		this.viewers.clear();
	}

	public void clear() {
		removeViewers();
		this.lines.clear();
		this.rawLines.clear();
		this.entities.clear();
	}

	private Pair<UUID, Integer> generateEntity() {
		UUID uuid = UUID.randomUUID();

		Location worldCenter = new Location(Bukkit.getWorlds().get(0), 0, 0, 0);
		Entity entity = worldCenter.getWorld().spawnEntity(worldCenter, EntityType.ARMOR_STAND);
		entity.remove();

		return Pair.of(uuid, entity.getEntityId());
	}

	private void spawnEntity(UUID uuid, Integer entityId, SimpleLocation location, String displayName,
			List<Player> players) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.SPAWN_ENTITY);

		packet.getIntegers().write(0, entityId);
		packet.getUUIDs().write(0, uuid);
		packet.getEntityTypeModifier().write(0, EntityType.ARMOR_STAND);

		packet.getDoubles().write(0, location.getX()).write(1, location.getY()).write(2, location.getZ());

		if (packet.getVectors().getFields().size() > 0) {
			packet.getVectors().write(0, new Vector(0, 0, 0));
		}

		players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));

		renameEntity(entityId, displayName, players);
	}

	private void renameEntity(Integer entityId, String newDisplayName, List<Player> players) {
		PacketContainer metadata = new PacketContainer(PacketType.Play.Server.ENTITY_METADATA);

		Serializer serializer = Registry.getChatComponentSerializer(true);

		metadata.getIntegers().write(0, entityId);
		metadata.getDataValueCollectionModifier().write(0,
				Arrays.asList(new WrappedDataValue(0, Registry.get((Type) Byte.class), (byte) (1 << 5)),
						new WrappedDataValue(2, serializer,
								Optional.of(WrappedChatComponent.fromLegacyText(newDisplayName).getHandle())),
						new WrappedDataValue(3, Registry.get((Type) Boolean.class), true),
						new WrappedDataValue(15, Registry.get((Type) Byte.class), (byte) ((1 << 0) | (1 << 4)))));

		players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, metadata));
	}

	private void teleportEntity(Integer entityId, SimpleLocation newLocation, List<Player> players) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_TELEPORT);

		packet.getIntegers().write(0, entityId);
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

	private void removeEntities(List<Integer> entityIds, List<Player> players) {
		PacketContainer packet = new PacketContainer(PacketType.Play.Server.ENTITY_DESTROY);

		packet.getIntLists().write(0, entityIds);

		players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));
	}

}
