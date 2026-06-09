package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;

public class PandaPrefixedStringField extends PandaStringField {

	@PandaInject
	private static PandaPluginClass plugin;

	public PandaPrefixedStringField(String path, String defaultContent) {
		super(path, defaultContent);
	}

	public PandaPrefixedStringField(String path, String oldPath, String defaultContent) {
		super(path, oldPath, defaultContent);
	}

	public String applyPrefix() {
		return plugin.getPrefix() + this.getContent();
	}

	public static String applyPrefix(String message) {
		return plugin != null ? (plugin.getPrefix() + message) : message;
	}

}
