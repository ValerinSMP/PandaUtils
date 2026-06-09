package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook.PandaAbstractWorldGuardHook;

public abstract class PandaAbstractWorldGuardAPI<T extends PandaAbstractWorldGuardHook> extends PandaAPIService<T> {

	@Override
	protected String getPluginName() {
		return "WorldGuard";
	}

}
