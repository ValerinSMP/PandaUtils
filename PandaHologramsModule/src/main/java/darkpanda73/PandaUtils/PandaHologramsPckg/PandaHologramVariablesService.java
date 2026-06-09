package darkpanda73.PandaUtils.PandaHologramsPckg;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaLongField;

@PandaService(loadOn = LoadStep.LOAD, priority = -999)
public class PandaHologramVariablesService {

	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_HOLOGRAMS_VIEWDISTANCE = new PandaLongField(
			"Settings.Holograms.View-distance", 16L);

	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_HOLOGRAMS_CACHE_TTL = new PandaLongField("Settings.Holograms.Cache.TTL",
			1000L * 60L * 2L);

	@PandaInject
	private PandaFilesService filesService;

	@LoadMethod
	private void load() {
		filesService.registerFields(this.getClass());
	}

}