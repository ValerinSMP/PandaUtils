package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Internal;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.session.handler.Handler;

public abstract class AbstractInternalWorldGuard {

	public abstract Object getRegionContainer() throws Exception;

	public abstract Object getFlagRegistry() throws Exception;

	public abstract Object getSessionManager() throws Exception;

	public abstract List<Flag<?>> getAllFlags() throws Exception;

	public abstract RegionManager getRegionManager(World world) throws Exception;

	public RegionManager getRegionManagerSafely(String worldName) {
		World world = Bukkit.getWorld(worldName);

		if (world == null) {
			return null;
		}

		return getRegionManagerSafely(world);
	}

	public RegionManager getRegionManagerSafely(World world) {
		try {
			return getRegionManager(world);
		} catch (Exception e) {
			return null;
		}
	}

	public abstract ApplicableRegionSet getApplicableRegions(Location location) throws Exception;

	public abstract com.sk89q.worldedit.world.World adapt(World world) throws Exception;

	public abstract com.sk89q.worldedit.util.Location adapt(Location location) throws Exception;

	public abstract com.sk89q.worldedit.entity.Player adapt(Player player) throws Exception;

	public abstract void registerHandler(Handler.Factory<?> handlerFactory) throws Exception;

	public abstract void registerHandler(Handler.Factory<?> handlerFactory, Handler.Factory<?> afterHandlerFactory)
			throws Exception;

	public LocalPlayer wrapPlayer(Player player) {
		return WorldGuardPlugin.inst().wrapPlayer(player);
	}

	public LocalPlayer wrapPlayer(UUID uuid) {
		return WorldGuardPlugin.inst().wrapOfflinePlayer(Bukkit.getOfflinePlayer(uuid));
	}

	public BlockVector3 asBlockVector(Location location) {
		return BlockVector3.at(location.getX(), location.getY(), location.getZ());
	}

}
