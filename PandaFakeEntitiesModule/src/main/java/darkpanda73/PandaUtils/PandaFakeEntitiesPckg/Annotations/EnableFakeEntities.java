package darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.PandaFakeEntitiesVariablesService;
import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.PandaFakeEntitiesService;
import darkpanda73.PandaUtils.PandaFakeEntitiesPckg.Listeners.PandaFakeEntitiesPlayerListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;

@PandaImport
@PandaListeners(PandaFakeEntitiesPlayerListener.class)
@PandaServices({ PandaFakeEntitiesService.class, PandaFakeEntitiesVariablesService.class })
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableFakeEntities {

}
