package darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.Hook;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public abstract class PandaAbstractPlaceholderHook extends PandaAbstractHook {

	private List<PlaceholderExpansion> registeredPlaceholders = new ArrayList<>();

	@Override
	public void load() throws Throwable {
		if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {

			for (PlaceholderExpansion placeholder : (registeredPlaceholders = getPlaceholders())) {
				placeholder.register();
			}

			hooked = true;
		} else {
			throw new Exception("PlaceholderAPI not found as plugin in the server.");
		}
	}

	@Override
	public void unload() throws Throwable {
		if (this.hooked) {
			for (PlaceholderExpansion placeholder : registeredPlaceholders) {
				PlaceholderAPI.unregisterExpansion(placeholder);
			}
		}
	}

	public String applyPlaceholders(String message, OfflinePlayer player) {
		return PlaceholderAPI.setPlaceholders(player, message);
	}

	public List<String> applyPlaceholders(List<String> message, OfflinePlayer player) {
		return PlaceholderAPI.setPlaceholders(player, message);
	}

	/**
	 * Warning: Make sure that you always give a new instance of the list, with a
	 * new instance of every placeholder, which data must be static after creation.
	 * 
	 * @return List with the placeholders to register.
	 */
	public abstract List<PlaceholderExpansion> getPlaceholders();

}
