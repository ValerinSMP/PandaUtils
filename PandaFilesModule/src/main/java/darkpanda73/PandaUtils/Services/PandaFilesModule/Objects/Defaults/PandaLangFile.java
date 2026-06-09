package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Defaults;

import java.io.IOException;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;

public class PandaLangFile extends PandaYamlFile {

	public PandaLangFile(PandaFolder parentFolder, PandaPluginClass plugin) throws IOException, YamlException {
		super("lang", parentFolder.getFile().getPath() + "/Lang.yml", new PandaYaml(plugin
				.getResource(plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Lang.yml")));
	}

}
