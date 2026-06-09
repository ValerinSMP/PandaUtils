package darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.PandaDiscordService;

@PandaImport
@PandaServices(PandaDiscordService.class)
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface EnableDiscordWebhookService {

}
