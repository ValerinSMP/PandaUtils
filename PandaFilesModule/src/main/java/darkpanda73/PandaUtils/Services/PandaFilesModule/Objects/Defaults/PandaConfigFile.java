package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults;

import java.io.IOException;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;

public class PandaConfigFile extends PandaYamlFile {

	public PandaConfigFile(PandaFolder parentFolder, PandaPluginClass plugin) throws IOException, YamlException {
		super("config", parentFolder.getFile().getPath() + "/Config.yml", new PandaYaml(plugin.getResource(
				plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Config.yml")));
	}

}
