package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.CoinsEngine;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.CoinsEngine.Hook.CoinsEngineHook;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class CoinsEngineAPI extends PandaAPIService<CoinsEngineHook> {

	@Override
	protected String getPluginName() {
		return "CoinsEngine";
	}

	@Override
	protected CoinsEngineHook newHook() throws Throwable {
		return new CoinsEngineHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}
