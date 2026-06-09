package darkpanda73.PandaUtils.PandaUtilities.Location;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@AllArgsConstructor
@Data
@EqualsAndHashCode
@Accessors(chain = true)
public class SimpleLocation implements Serializable {

	private static final long serialVersionUID = 1566216246051511186L;

	private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> HIGHER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getX() >= loc2.getX() && loc1.getY() >= loc2.getY() && loc1.getZ() >= loc2.getZ();
	private static final BiFunction<SimpleLocation, SimpleLocation, Boolean> LOWER_OR_EQUAL_THAN_CHECKING = (loc1,
			loc2) -> loc1.getX() <= loc2.getX() && loc1.getY() <= loc2.getY() && loc1.getZ() <= loc2.getZ();

	private String worldName;
	private double x;
	private double y;
	private double z;
	private float yaw;
	private float pitch;

	public SimpleLocation(String worldName, double x, double y, double z) {
		this(worldName, x, y, z, 0F, 0F);
	}

	public int getBlockX() {
		return Location.locToBlock(this.x);
	}

	public int getBlockY() {
		return Location.locToBlock(this.y);
	}

	public int getBlockZ() {
		return Location.locToBlock(this.z);
	}

	public SimpleChunk getChunk() {
		return new SimpleChunk(worldName, getBlockX() >> 4, getBlockZ() >> 4);
	}

	public SimpleLocation add(SimpleLocation location) {
		return add(location.getX(), location.getY(), location.getZ());
	}

	public SimpleLocation add(double x, double y, double z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public SimpleLocation add(int x, int y, int z) {
		this.x += x;
		this.y += y;
		this.z += z;
		return this;
	}

	public SimpleLocation substract(SimpleLocation location) {
		return substract(location.getX(), location.getY(), location.getZ());
	}

	public SimpleLocation substract(double x, double y, double z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public SimpleLocation substract(int x, int y, int z) {
		this.x -= x;
		this.y -= y;
		this.z -= z;
		return this;
	}

	public SimpleLocation clone() {
		return new SimpleLocation(worldName, x, y, z, yaw, pitch);
	}

	public Location toLocation() {
		World world = Bukkit.getWorld(worldName);
		return world != null ? new Location(Bukkit.getWorld(worldName), x, y, z, yaw, pitch) : null;
	}

	public double distance(SimpleLocation location) {
		return Math.sqrt(Math.abs(Math.pow((this.getX() - location.getX()), 2)
				+ Math.pow((this.getY() - location.getY()), 2) + Math.pow((this.getZ() - location.getZ()), 2)));
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();

		builder.append("(world) ");
		builder.append(worldName);
		builder.append(" (x) ");
		builder.append(x);
		builder.append(" (y) ");
		builder.append(y);
		builder.append(" (z) ");
		builder.append(z);

		return builder.toString();
	}

	public static SimpleLocation of(Location location) {
		return new SimpleLocation(location.getWorld().getName(), location.getX(), location.getY(), location.getZ(),
				location.getYaw(), location.getPitch());
	}

	@Data
	@AllArgsConstructor
	public static class SimpleChunk implements Serializable {

		private static final long serialVersionUID = -8818462982403873655L;

		private String worldName;
		private int x;
		private int z;

		public SimpleLocation getMinLocation() {
			return new SimpleLocation(worldName, x << 4, Integer.MIN_VALUE, z << 4);
		}

		public SimpleLocation getMaxLocation() {
			return new SimpleLocation(worldName, (x << 4) + 15, Integer.MAX_VALUE, (z << 4) + 15);
		}

		public SimpleLocationArea getLocationArea() {
			return new SimpleLocationArea(worldName, getMinLocation(), getMaxLocation().add(1D, 0, 1D));
		}

		public boolean isLoaded() {
			return Bukkit.getWorld(worldName).isChunkLoaded(x, z);
		}

		public Chunk toChunk() {
			return Bukkit.getWorld(worldName).getChunkAt(x, z);
		}

		public static SimpleChunk of(Chunk chunk) {
			return new SimpleChunk(chunk.getWorld().getName(), chunk.getX(), chunk.getZ());
		}

	}

	@Data
	@Accessors(chain = true)
	public static class SimpleLocationArea implements Serializable {

		private static final long serialVersionUID = -1227129061896152323L;

		private String worldName;
		private SimpleLocation minLocation;
		private SimpleLocation maxLocation;

		public SimpleLocationArea(String worldName, Location location1, Location location2) {
			this.worldName = worldName;
			this.minLocation = new SimpleLocation(worldName, Math.min(location1.getX(), location2.getX()),
					Math.min(location1.getY(), location2.getY()), Math.min(location1.getZ(), location2.getZ()));
			this.maxLocation = new SimpleLocation(worldName, Math.max(location1.getX(), location2.getX()),
					Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
		}

		public SimpleLocationArea(String worldName, SimpleLocation location1, SimpleLocation location2) {
			this.worldName = worldName;
			this.minLocation = new SimpleLocation(worldName, Math.min(location1.getX(), location2.getX()),
					Math.min(location1.getY(), location2.getY()), Math.min(location1.getZ(), location2.getZ()));
			this.maxLocation = new SimpleLocation(worldName, Math.max(location1.getX(), location2.getX()),
					Math.max(location1.getY(), location2.getY()), Math.max(location1.getZ(), location2.getZ()));
		}

		public SimpleLocation getCenterLocation() {
			return new SimpleLocation(this.worldName, (minLocation.getX() + maxLocation.getX()) / 2,
					(minLocation.getY() + maxLocation.getY()) / 2, (minLocation.getZ() + maxLocation.getZ()) / 2);
		}

		public boolean isInside(SimpleLocation location) {
			return HIGHER_OR_EQUAL_THAN_CHECKING.apply(location, minLocation)
					&& LOWER_OR_EQUAL_THAN_CHECKING.apply(location, maxLocation)
					&& (getWorldName() == null || getWorldName().equals(location.getWorldName()));
		}

		public boolean isInside(SimpleLocationArea locationArea) {
			return HIGHER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMaxLocation(), minLocation)
					&& LOWER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMinLocation(), maxLocation)
					&& (getWorldName() == null || getWorldName().equals(locationArea.getWorldName()));
		}

		public boolean isAllInside(SimpleLocation location) {
			return HIGHER_OR_EQUAL_THAN_CHECKING.apply(location, minLocation)
					&& LOWER_OR_EQUAL_THAN_CHECKING.apply(location, maxLocation)
					&& (getWorldName() == null || getWorldName().equals(location.getWorldName()));
		}

		public boolean isAllInside(SimpleLocationArea locationArea) {
			return HIGHER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMinLocation(), minLocation)
					&& LOWER_OR_EQUAL_THAN_CHECKING.apply(locationArea.getMaxLocation(), maxLocation)
					&& (getWorldName() == null || getWorldName().equals(locationArea.getWorldName()));
		}

		public List<SimpleChunk> getChunks() {
			List<SimpleChunk> chunks = new ArrayList<>();

			SimpleChunk minChunk = getMinLocation().getChunk();
			SimpleChunk maxChunk = getMaxLocation().getChunk();

			for (int x = minChunk.getX(); x <= maxChunk.getX(); x++) {
				for (int z = minChunk.getZ(); z <= maxChunk.getZ(); z++) {
					chunks.add(new SimpleChunk(worldName, x, z));
				}
			}

			return chunks;
		}

		public SimpleLocationArea clone() {
			return new SimpleLocationArea(worldName, minLocation.clone(), maxLocation.clone());
		}

		public static SimpleLocationArea of(SimpleLocation location1, SimpleLocation location2) {
			return new SimpleLocationArea(location1.getWorldName(), location1, location2);
		}

		public static SimpleLocationArea of(Location location1, Location location2) {
			return new SimpleLocationArea(location1.getWorld().getName(), location1, location2);
		}

	}

}
