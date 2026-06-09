package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.SetFlag;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import lombok.NonNull;

public abstract class AbstractStringSetFlag extends AbstractFlag<SetFlag<String>> {

	public AbstractStringSetFlag(@NonNull PandaAbstractWorldGuardHook worldGuardApi) {
		super(worldGuardApi);
	}

	@Override
	public void registerFlag() {
		try {
			FlagRegistry fr = (FlagRegistry) getWorldGuardHook().getInternalWorldGuard().getFlagRegistry();

			SetFlag<String> f = new SetFlag<String>(getFlagName(), new StringFlag(null));
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

	public Set<String> flagSet(ProtectedRegion region) {
		Set<String> set = region.getFlag(worldGuardFlag);

		return set != null ? set : new HashSet<>();
	}

	public Set<String> flagSet(Location loc) throws Exception {
		if (this.worldGuardFlag == null) {
			return null;
		}

		ApplicableRegionSet set = getWorldGuardHook().getInternalWorldGuard().getApplicableRegions(loc);

		if (set == null) {
			return new HashSet<>();
		}

		Set<String> value = set.getRegions().size() != 0
				? set.getRegions().toArray(new ProtectedRegion[set.getRegions().size()])[set.getRegions().size()]
						.getFlag(worldGuardFlag)
				: null;

		return value != null ? value : new HashSet<>();
	}

	public Set<String> flagState(Player pl) throws Exception {
		if (this.worldGuardFlag == null) {
			return null;
		}

		ApplicableRegionSet set = getWorldGuardHook().getInternalWorldGuard().getApplicableRegions(pl.getLocation());

		if (set == null) {
			return new HashSet<>();
		}

		Set<String> value = set.queryValue(getWorldGuardHook().getInternalWorldGuard().wrapPlayer(pl), worldGuardFlag);

		return value != null ? value : new HashSet<>();
	}

}
