package darkpanda73.PandaUtils.PandaUtilities.Location;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import darkpanda73.PandaUtils.PandaUtilities.WorldUtilities;
import lombok.Getter;
import lombok.Setter;

@Getter
public class LocationReference {

	private String worldName;
	private int x;
	private int y;
	private int z;

	private @Setter boolean cacheValue = true;
	private Location location;

	public LocationReference(Location location) {
		this(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
		this.location = location;
	}

	public LocationReference(String worldName, int x, int y, int z) {
		this.worldName = worldName;
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Location toLocation() {
		if (this.cacheValue && this.location != null) {
			return this.location;
		}

		World world = Bukkit.getWorld(worldName);
		if (world != null) {
			int min = WorldUtilities.getMinHeight(world);
			int max = world.getMaxHeight();
			Location location = new Location(world, x, (y < min ? min : (y > max ? max : y)), z);
			return this.cacheValue ? (this.location = location) : location;
		}

		return null;
	}

	public static LocationReference fromLocation(Location location) {
		return new LocationReference(location);
	}

}
