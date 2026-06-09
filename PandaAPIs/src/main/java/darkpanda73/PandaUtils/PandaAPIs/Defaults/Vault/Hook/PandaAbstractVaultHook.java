package darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault.Hook;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import lombok.Getter;
import net.milkbowl.vault.economy.Economy;

public abstract class PandaAbstractVaultHook extends PandaAbstractHook {

	private @Getter boolean hooked;
	private @Getter Economy economy;

	@Override
	public void load() throws Throwable {
		hooked = false;
		if (Bukkit.getServer().getPluginManager().getPlugin("Vault") != null) {
			RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager()
					.getRegistration(Economy.class);

			if (rsp != null) {
				economy = rsp.getProvider();

				hooked = true;
			} else {
				throw new Exception(
						"Registered service provider for the Economy class has not been found. Probably no economy plugin installed.");
			}
		} else {
			throw new Exception("Vault not found as plugin in the server.");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public boolean deposit(Player pl, double amount) {
		return getEconomy().depositPlayer(pl, amount).transactionSuccess();
	}

	public boolean withdraw(Player pl, double amount) {
		return getEconomy().withdrawPlayer(pl, amount).transactionSuccess();
	}

}
