package darkpanda73.PandaUtils.Services.PandaBossBarModule.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.PandaBossBarService;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.Listeners.PandaBossBarListener;

@PandaImport
@PandaServices(PandaBossBarService.class)
@PandaListeners(PandaBossBarListener.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface EnableBossBarService {

}
