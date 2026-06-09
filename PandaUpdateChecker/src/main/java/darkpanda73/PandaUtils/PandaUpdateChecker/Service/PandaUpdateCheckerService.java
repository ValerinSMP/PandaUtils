package darkpanda73.PandaUtils.PandaUpdateChecker.Service;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaUpdateChecker.Annotations.EnableUpdateChecker;
import darkpanda73.PandaUtils.PandaUpdateChecker.Events.PandaUpdateCheckerListener;
import darkpanda73.PandaUtils.PandaUpdateChecker.Utils.PandaUpdateCheckerUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import lombok.Getter;

@PandaService
public class PandaUpdateCheckerService {

	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_UPDATECHECKER_ENABLED = new PandaBooleanField(
			"Settings.Update-checker.Enabled", true);
	@RegisteredPandaField("config")
	public static PandaBooleanField SETTINGS_UPDATECHECKER_JOINMESSAGE = new PandaBooleanField(
			"Settings.Update-checker.Join-message", true);

	private static final String URL_TEMPLATE = "https://www.spigotmc.org/resources/";

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private String spigotResourceId;
	private @Getter String fetchedVersion;

	@PostInjectMethod
	private void postInject() {
		if (filesService != null) {
			filesService.registerFields(PandaUpdateCheckerService.class);
			filesService.registerFields(PandaUpdateCheckerListener.class);

			EnableUpdateChecker annotation = plugin.getClass().getAnnotation(EnableUpdateChecker.class);
			if (annotation != null) {
				this.spigotResourceId = annotation.value();
			}
		}
	}

	@LoadMethod
	private void load() {
		if (spigotResourceId != null && !spigotResourceId.isEmpty() && SETTINGS_UPDATECHECKER_ENABLED.getContent()) {
			PandaUpdateCheckerUtilities.getVersion(this.spigotResourceId, (version) -> {
				fetchedVersion = version;

				if (isOutdated()) {
					plugin.sendError(this.getClass(), "*--------------------------------------------*");
					plugin.sendError(this.getClass(), "");
					plugin.sendError(this.getClass(),
							String.format("  &cA new version has been found &8(&e%s&8)", fetchedVersion));
					plugin.sendError(this.getClass(), "  &cVisit the new URL to download the last version:");
					plugin.sendError(this.getClass(), "  &7" + getUrl());
					plugin.sendError(this.getClass(), "");
					plugin.sendError(this.getClass(), "*--------------------------------------------*");
				}
			});
		}
	}

	public boolean isOutdated() {
		return fetchedVersion != null && !fetchedVersion.equals(plugin.getDescription().getVersion());
	}

	public String getUrl() {
		return URL_TEMPLATE + this.spigotResourceId;
	}

}
