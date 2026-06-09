package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.PlayerPoints;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.PlayerPoints.Hook.PlayerPointsHook;

@PandaService(priority = -1, loadOn = LoadStep.ENABLE)
public class PlayerPointsAPI extends PandaAPIService<PlayerPointsHook> {

	@Override
	protected String getPluginName() {
		return "PlayerPoints";
	}

	@Override
	protected PlayerPointsHook newHook() throws Throwable {
		return new PlayerPointsHook();
	}

	@Override
	protected boolean isRequired() {
		return false;
	}

}
