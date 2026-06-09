package darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces;

import org.bukkit.entity.Player;

public interface IEconomyHook {

	public abstract boolean withdraw(Player player, double amount);

	public abstract boolean deposit(Player player, double amount);

	public abstract double getBalance(Player player);

}
