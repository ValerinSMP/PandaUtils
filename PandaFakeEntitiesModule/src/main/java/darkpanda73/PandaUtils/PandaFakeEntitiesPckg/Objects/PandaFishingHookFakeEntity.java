package darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Objects;

import java.util.List;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;

import darkpanda73.PandaUtils.PandaUtilities.Location.SimpleLocation;
import lombok.Getter;
import lombok.ToString;

@ToString
public class PandaFishingHookFakeEntity extends PandaFakeEntity {

	private @Getter EntityType entityType;
	private @Getter int ownerId;

	public PandaFishingHookFakeEntity(SimpleLocation location, int ownerId) {
		super(location);

		try {
			entityType = EntityType.valueOf("FISHING_HOOK");
		} catch (IllegalArgumentException e) {
			entityType = EntityType.valueOf("FISHING_BOBBING");
		}

		this.ownerId = ownerId;
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

		packet.getIntegers().write(4, this.ownerId);

		players.forEach(player -> ProtocolLibrary.getProtocolManager().sendServerPacket(player, packet));
	}

}
