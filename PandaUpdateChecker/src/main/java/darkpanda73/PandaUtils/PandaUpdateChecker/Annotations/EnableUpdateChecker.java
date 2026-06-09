package darkpanda73.PandaUtils.PandaUpdateChecker.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListeners;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.PandaUpdateChecker.Events.PandaUpdateCheckerListener;
import darkpanda73.PandaUtils.PandaUpdateChecker.Service.PandaUpdateCheckerService;

@PandaImport
@PandaListeners(PandaUpdateCheckerListener.class)
@PandaServices(PandaUpdateCheckerService.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableUpdateChecker {

	public String value();

}
