package darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI;

import darkpanda73.PandaUtils.PandaAPIs.PandaAPIService;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.Hook.PandaAbstractPlaceholderHook;

public abstract class PandaAbstractPlaceholderAPI<T extends PandaAbstractPlaceholderHook> extends PandaAPIService<T> {

	@Override
	protected String getPluginName() {
		return "PlaceholderAPI";
	}

	@Override
	public void reload() throws Throwable {
		// Reload is not allowed on PlaceholderAPI
	}

}
