package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.EssentialsX.Hook;

import java.math.BigDecimal;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.earth2me.essentials.Essentials;
import com.earth2me.essentials.User;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;
import net.ess3.api.MaxMoneyException;

public class EssentialsXHook extends PandaAbstractHook implements IEconomyHook {

	private Essentials api;

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().isPluginEnabled("Essentials")) {
			this.api = (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");

			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find Essentials data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	@Override
	public boolean withdraw(Player player, double amount) {
		try {
			this.api.getUser(player).giveMoney(BigDecimal.valueOf(amount));
			return true;
		} catch (MaxMoneyException e) {
			return false;
		}
	}

	@Override
	public boolean deposit(Player player, double amount) {
		User user = this.api.getUser(player);
		if (user.canAfford(BigDecimal.valueOf(amount))) {
			this.api.getUser(player).takeMoney(BigDecimal.valueOf(amount));
			return true;
		}
		return false;
	}

	@Override
	public double getBalance(Player player) {
		return this.api.getUser(player).getMoney().doubleValue();
	}

}