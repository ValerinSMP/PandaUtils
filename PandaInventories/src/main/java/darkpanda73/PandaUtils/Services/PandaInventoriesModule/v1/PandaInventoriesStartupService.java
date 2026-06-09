package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.PandaAbstractPlaceholderAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.InventoryItemBuilder;

@PandaService(injectable = true, loadOn = LoadStep.LOAD, priority = -1000)
public class PandaInventoriesStartupService {

	@RegisteredPandaField("config")
	public static final PandaBooleanField INVENTORIES_PLACEHOLDERAPISUPPORT = new PandaBooleanField(
			"Inventories.PlaceholderAPI-support", false);

	@PandaInject
	private PandaAbstractPlaceholderAPI<?> placeholderApi;

	@PandaInject
	private PandaFilesService filesService;

	@LoadMethod
	private void load() {
		if (placeholderApi != null) {
			InjectUtils.injectOnField(Item.class, "placeholderApi", placeholderApi);
			InjectUtils.injectOnField(InventoryItemBuilder.class, "placeholderApi", placeholderApi);
		}

		filesService.getYaml("config").registerFieldsFromClass(this.getClass());
	}

}
