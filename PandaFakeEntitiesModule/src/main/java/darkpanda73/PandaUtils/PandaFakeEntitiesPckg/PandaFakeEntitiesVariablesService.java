package darkpanda73.PandaUtils.PandaFakeEntitiesPckg;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaLongField;

@PandaService(loadOn = LoadStep.LOAD, priority = -999)
public class PandaFakeEntitiesVariablesService {

	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_FAKEENTITIES_VIEWDISTANCE = new PandaLongField(
			"Settings.Fake-entities.View-distance", 16L);

	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_FAKEENTITIES_CACHE_TTL = new PandaLongField(
			"Settings.Fake-entities.Cache.TTL", 1000L * 60L * 2L);

	@PandaInject
	private PandaFilesService filesService;

	@LoadMethod
	private void load() {
		filesService.registerFields(this.getClass());
	}

}