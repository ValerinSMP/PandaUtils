package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.Vault;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault.PandaAbstractVaultAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.Vault.Hook.VaultHook;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class VaultAPI extends PandaAbstractVaultAPI<VaultHook> {

	@Override
	protected VaultHook newHook() throws Throwable {
		return new VaultHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}
