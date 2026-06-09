package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.CoinsEngine.Hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;
import su.nightexpress.coinsengine.api.CoinsEngineAPI;
import su.nightexpress.coinsengine.api.currency.Currency;

public class CoinsEngineHook extends PandaAbstractHook implements IEconomyHook {

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().isPluginEnabled("CoinsEngine")) {
			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find CoinsEngine data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	@Override
	public boolean withdraw(Player player, double amount) {
		Currency currency = CoinsEngineAPI.getCurrencyManager().getVaultCurrency().orElse(null);

		if (currency != null) {
			return CoinsEngineAPI.removeBalance(player.getUniqueId(), currency, amount);
		}

		return false;
	}

	@Override
	public boolean deposit(Player player, double amount) {
		Currency currency = CoinsEngineAPI.getCurrencyManager().getVaultCurrency().orElse(null);

		if (currency != null) {
			return CoinsEngineAPI.addBalance(player.getUniqueId(), currency, amount);
		}

		return false;
	}

	@Override
	public double getBalance(Player player) {
		Currency currency = CoinsEngineAPI.getCurrencyManager().getVaultCurrency().orElse(null);
		if (currency != null) {
			return CoinsEngineAPI.getBalance(player, currency);
		}
		return 0D;
	}

}