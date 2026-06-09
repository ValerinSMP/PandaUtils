package darkpanda73.PandaUtils.PandaHologramsPckg.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaHologramsPckg.PandaHologramsService;
import darkpanda73.PandaUtils.PandaHologramsPckg.PandaHologramVariablesService;
import darkpanda73.PandaUtils.PandaHologramsPckg.Listeners.PandaHologramPlayerListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;

@PandaImport
@PandaListeners(PandaHologramPlayerListener.class)
@PandaServices({ PandaHologramsService.class, PandaHologramVariablesService.class })
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableHolograms {

}
