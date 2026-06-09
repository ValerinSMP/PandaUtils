package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.TokenManager.Hook;

import java.util.OptionalLong;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;
import me.realized.tokenmanager.api.TokenManager;

public class TokenManagerHook extends PandaAbstractHook implements IEconomyHook {

	private TokenManager api;

	@Override
	public void load() throws Throwable {
		this.api = (TokenManager) Bukkit.getServer().getPluginManager().getPlugin("TokenManager");

		if (this.api != null) {
			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find TokenManager data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	@Override
	public boolean withdraw(Player player, double amount) {
		OptionalLong current = this.api.getTokens(player);
		if (!current.isPresent() || current.getAsLong() < amount) {
			return false;
		}

		return this.api.removeTokens(player, (long) amount);
	}

	@Override
	public boolean deposit(Player player, double amount) {
		return this.api.addTokens(player, (long) amount);
	}

	@Override
	public double getBalance(Player player) {
		return this.api.getTokens(player).getAsLong();
	}

}
