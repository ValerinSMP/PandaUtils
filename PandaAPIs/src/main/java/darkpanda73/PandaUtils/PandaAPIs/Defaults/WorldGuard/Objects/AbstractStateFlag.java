package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;

public abstract class AbstractStateFlag extends AbstractFlag<StateFlag> {

	public AbstractStateFlag(PandaAbstractWorldGuardHook worldGuardHook) {
		super(worldGuardHook);
	}

	@Override
	public void registerFlag() {
		try {
			FlagRegistry fr = (FlagRegistry) getWorldGuardHook().getInternalWorldGuard().getFlagRegistry();

			StateFlag f = new StateFlag(getFlagName(), getFlagDefaultValue());
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

	public abstract boolean getFlagDefaultValue();

	public State flagState(Location loc) throws Exception {
		if (this.worldGuardFlag == null) {
			return null;
		}

		ApplicableRegionSet set = getWorldGuardHook().getInternalWorldGuard().getApplicableRegions(loc);

		if (set == null) {
			return null;
		}

		State state = set.getRegions().size() != 0
				? set.getRegions().toArray(new ProtectedRegion[set.getRegions().size()])[set.getRegions().size()]
						.getFlag(worldGuardFlag)
				: null;

		return state != null ? state : getFlagDefaultValue() ? State.ALLOW : State.DENY;
	}

	public State flagState(Player pl) throws Exception {
		if (this.worldGuardFlag == null) {
			return null;
		}

		ApplicableRegionSet set = getWorldGuardHook().getInternalWorldGuard().getApplicableRegions(pl.getLocation());

		if (set == null) {
			return null;
		}

		State state = set.queryValue(getWorldGuardHook().getInternalWorldGuard().wrapPlayer(pl), worldGuardFlag);

		return state != null ? state : (getFlagDefaultValue() ? State.ALLOW : State.DENY);
	}

}
