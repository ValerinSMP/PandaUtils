package darkpanda73.PandaUtils.Services.PandaEconomiesModule;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.CoinsEngine.CoinsEngineAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.EssentialsX.EssentialsXAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.PlayerPoints.PlayerPointsAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.TokenManager.TokenManagerAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.Vault.VaultAPI;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Enums.EconomyService;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;

@PandaService(loadOn = LoadStep.LOAD)
public class PandaEconomiesService {

	@PandaInject
	private VaultAPI vaultApi;

	@PandaInject
	private TokenManagerAPI tokenManagerApi;

	@PandaInject
	private PlayerPointsAPI playerPointsApi;

	@PandaInject
	private EssentialsXAPI essentialsXApi;

	@PandaInject
	private CoinsEngineAPI coinsEngineApi;

	private Map<EconomyService, Supplier<IEconomyHook>> economyHooks = new HashMap<>();

	public PandaEconomiesService() {
		economyHooks.put(EconomyService.VAULT, () -> vaultApi.isHooked() ? vaultApi.getHook() : null);
		economyHooks.put(EconomyService.TOKENMANAGER,
				() -> tokenManagerApi.isHooked() ? tokenManagerApi.getHook() : null);
		economyHooks.put(EconomyService.PLAYERPOINTS,
				() -> playerPointsApi.isHooked() ? playerPointsApi.getHook() : null);
		economyHooks.put(EconomyService.ESSENTIALSX, () -> essentialsXApi.isHooked() ? essentialsXApi.getHook() : null);
		economyHooks.put(EconomyService.COINSENGINE, () -> coinsEngineApi.isHooked() ? coinsEngineApi.getHook() : null);
	}

	public boolean withdraw(Player player, double amount) {
		return withdraw(null, player, amount);
	}

	public boolean withdraw(EconomyService economyService, Player player, double amount) {
		IEconomyHook hook = getEconomyHookOrRandom(economyService);
		return hook != null ? hook.withdraw(player, amount) : false;
	}

	public boolean deposit(Player player, double amount) {
		return deposit(null, player, amount);
	}

	public boolean deposit(EconomyService economyService, Player player, double amount) {
		IEconomyHook hook = getEconomyHookOrRandom(economyService);
		return hook != null ? hook.deposit(player, amount) : false;
	}

	public boolean canAfford(Player player, double amount) {
		return canAfford(null, player, amount);
	}

	public boolean canAfford(EconomyService economyService, Player player, double amount) {
		return getBalance(economyService, player) >= amount;
	}

	public double getBalance(Player player) {
		return getBalance(null, player);
	}

	public double getBalance(EconomyService economyService, Player player) {
		IEconomyHook hook = getEconomyHookOrRandom(economyService);
		return hook != null ? hook.getBalance(player) : 0D;
	}

	public boolean isEconomyEnabled(EconomyService economyService) {
		return economyHooks.get(economyService).get() != null;
	}

	private IEconomyHook getEconomyHookOrRandom(EconomyService economyService) {
		IEconomyHook hook = economyService != null ? economyHooks.get(economyService).get() : null;

		if (hook == null && economyService == null) {
			hook = economyHooks.values().stream().map(supp -> supp.get()).filter(Objects::nonNull).findFirst()
					.orElse(null);
		}

		return hook;
	}

}
