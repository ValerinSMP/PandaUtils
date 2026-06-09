package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesStartupService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Events.PandaInventoryEvents;

@PandaImport
@PandaServices({ PandaInventoriesService.class, PandaInventoriesStartupService.class })
@PandaListeners(PandaInventoryEvents.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface EnableInventoriesService {

}
