package darkpanda73.PandaUtils.Services.PandaEconomiesModule.EconomyServices.PlayerPoints.Hook;

import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.Services.PandaEconomiesModule.Interfaces.IEconomyHook;

public class PlayerPointsHook extends PandaAbstractHook implements IEconomyHook {

	private PlayerPointsAPI api;

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
			this.api = PlayerPoints.getInstance().getAPI();

			this.hooked = true;
		} else {
			throw new NullPointerException("Unable to find PlayerPoints data");
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	@Override
	public boolean withdraw(Player player, double amount) {
		return this.api.take(player.getUniqueId(), (int) amount);
	}

	@Override
	public boolean deposit(Player player, double amount) {
		return this.api.give(player.getUniqueId(), (int) amount);
	}

	@Override
	public double getBalance(Player player) {
		return this.api.look(player.getUniqueId());
	}

}