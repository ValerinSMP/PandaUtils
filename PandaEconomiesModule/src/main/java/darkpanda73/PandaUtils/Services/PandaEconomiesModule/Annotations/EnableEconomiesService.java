package darkpanda73.PandaUtils.Services.PandaEconomiesModule.Annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaImport;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaServices;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.PandaEconomiesService;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.CoinsEngine.CoinsEngineAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.EssentialsX.EssentialsXAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.PlayerPoints.PlayerPointsAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.TokenManager.TokenManagerAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.Vault.VaultAPI;

@PandaImport
@PandaServices({ EssentialsXAPI.class, PlayerPointsAPI.class, TokenManagerAPI.class, VaultAPI.class,
		CoinsEngineAPI.class, PandaEconomiesService.class })
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.ANNOTATION_TYPE, ElementType.TYPE })
public @interface EnableEconomiesService {

}
