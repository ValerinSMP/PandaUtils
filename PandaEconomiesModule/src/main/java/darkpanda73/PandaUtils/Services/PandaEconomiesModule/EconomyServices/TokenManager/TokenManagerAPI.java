package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.TokenManager;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.TokenManager.Hook.TokenManagerHook;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class TokenManagerAPI extends PandaAPIService<TokenManagerHook> {

	@Override
	protected String getPluginName() {
		return "TokenManager";
	}

	@Override
	protected TokenManagerHook newHook() throws Throwable {
		return new TokenManagerHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}
