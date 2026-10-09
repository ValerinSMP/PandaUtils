package darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Listeners;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;

public class PandaMessageListener implements Listener {

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_MESSAGELISTENER_LISTENERPRIORITY = new PandaStringField(
			"Settings.Message-listener.Cancel-phrase", "cancel");

	private ConcurrentHashMap<UUID, Callback> listeningTo = new ConcurrentHashMap<>();

	public void startListening(UUID playerUuid, Callback callback) throws PlayerAlreadyListeningException {
		if (listeningTo.containsKey(playerUuid)) {
			throw new PlayerAlreadyListeningException();
		}
		listeningTo.put(playerUuid, callback);
	}

	public void replaceListening(UUID playerUuid, Callback callback) {
		listeningTo.put(playerUuid, callback);
	}

	public void stopListening(UUID playerUuid) {
		Callback callback = listeningTo.remove(playerUuid);
		if (callback != null) {
			callback.cancel();
		}
	}

	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {
		stopListening(e.getPlayer().getUniqueId());
	}

	public void onPlayerChatEvent(AsyncPlayerChatEvent e) {
		Callback callback = listeningTo.get(e.getPlayer().getUniqueId());
		if (callback != null) {
			e.setCancelled(true);

			UUID playerUuid = e.getPlayer().getUniqueId();
			String message = e.getMessage();
			TasksUtils.execute(() -> {
				if (listeningTo.get(playerUuid) != callback) {
					return;
				}

				boolean cancelled = SETTINGS_MESSAGELISTENER_LISTENERPRIORITY.hasContent()
						&& SETTINGS_MESSAGELISTENER_LISTENERPRIORITY.getContent().equalsIgnoreCase(message);
				if (cancelled || callback.message(message)) {
					listeningTo.remove(playerUuid, callback);
					callback.cancel();
				}
			});
		}
	}

	public interface Callback {

		public abstract boolean message(String message);

		public default void cancel() {
		}

	}

}
