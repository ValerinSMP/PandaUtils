package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import com.sk89q.worldguard.protection.flags.LocationFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;

public abstract class AbstractLocationFlag extends AbstractFlag<LocationFlag> {

	public AbstractLocationFlag(PandaAbstractWorldGuardHook worldGuardHook) {
		super(worldGuardHook);
	}

	@Override
	public void registerFlag() {
		try {
			FlagRegistry fr = (FlagRegistry) getWorldGuardHook().getInternalWorldGuard().getFlagRegistry();

			LocationFlag f = new LocationFlag(getFlagName());

			fr.register(f);
			worldGuardFlag = f;
		} catch (FlagConflictException e) {
			MessageTemplate
					.inst(getWorldGuardHook().getPlugin().getPrefix()
							.concat("Seems like another flag is conflicting the flag '" + getFlagName() + "'."))
					.process().sendMessage(Bukkit.getConsoleSender());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public abstract String getFlagName();

	public Location flagState(World world, ProtectedRegion protectedRegion) {
		com.sk89q.worldedit.util.Location loc = protectedRegion.getFlag(getWorldGuardFlag());

		return loc != null ? new Location(world, loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()) : null;
	}

}
