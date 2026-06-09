package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Services.PandaInputListenerService;

@PandaImport
@PandaServices(PandaInputListenerService.class)
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableInputListenerService {

}
