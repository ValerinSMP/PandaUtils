package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.Vault.Hook;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.Vault.Hook.PandaAbstractVaultHook;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;

public class VaultHook extends PandaAbstractVaultHook implements IEconomyHook {

	@Override
	public double getBalance(Player player) {
		return this.getEconomy().getBalance(player);
	}

}
