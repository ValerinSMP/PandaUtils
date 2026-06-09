package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Internal;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.session.handler.Handler.Factory;

public class InternalNewWorldGuard extends AbstractInternalWorldGuard {

	@Override
	public Object getRegionContainer() throws Exception {
		return WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	@Override
	public Object getFlagRegistry() throws Exception {
		return WorldGuard.getInstance().getFlagRegistry();
	}

	@Override
	public Object getSessionManager() throws Exception {
		return WorldGuard.getInstance().getPlatform().getSessionManager();
	}

	@Override
	public List<Flag<?>> getAllFlags() throws Exception {
		return WorldGuard.getInstance().getFlagRegistry().getAll();
	}

	@Override
	public RegionManager getRegionManager(World world) throws Exception {
		return WorldGuard.getInstance().getPlatform().getRegionContainer().get(adapt(world));
	}

	@Override
	public ApplicableRegionSet getApplicableRegions(Location location) throws Exception {
		return getRegionManager(location.getWorld()).getApplicableRegions(asBlockVector(location));
	}

	@Override
	public com.sk89q.worldedit.world.World adapt(World world) throws Exception {
		return BukkitAdapter.adapt(world);
	}

	@Override
	public com.sk89q.worldedit.util.Location adapt(Location location) throws Exception {
		return BukkitAdapter.adapt(location);
	}

	@Override
	public com.sk89q.worldedit.entity.Player adapt(Player player) throws Exception {
		return BukkitAdapter.adapt(player);
	}

	@Override
	public void registerHandler(Factory<?> handlerFactory) throws Exception {
		registerHandler(handlerFactory, null);
	}

	@Override
	public void registerHandler(Factory<?> handlerFactory, Factory<?> afterHandlerFactory) throws Exception {
		WorldGuard.getInstance().getPlatform().getSessionManager().registerHandler(handlerFactory, afterHandlerFactory);
	}

}
