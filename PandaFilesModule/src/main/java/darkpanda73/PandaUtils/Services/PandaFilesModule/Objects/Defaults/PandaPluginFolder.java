package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;

public class PandaPluginFolder extends PandaFolder {

	public PandaPluginFolder(PandaPluginClass plugin) {
		super("plugin", "plugins/" + plugin.getName());
	}

}
