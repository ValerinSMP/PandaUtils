package darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault.Hook.PandaAbstractVaultHook;

public abstract class PandaAbstractVaultAPI<T extends PandaAbstractVaultHook> extends PandaAPIService<T> {

	@Override
	protected String getPluginName() {
		return "Vault";
	}

}
