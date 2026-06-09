package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects;

import com.sk89q.worldguard.protection.flags.Flag;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public abstract class AbstractFlag<T extends Flag<?>> {

	private @NonNull PandaAbstractWorldGuardHook worldGuardHook;

	protected T worldGuardFlag;

	public abstract void registerFlag();

}
