package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects;

import java.util.UUID;

import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Exceptions.PlayerNotListenedException;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Services.PandaInputListenerService;
import lombok.Getter;
import lombok.Setter;

public abstract class InputListener<T> {

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_INPUTLISTENER_CANCELPHRASE = new PandaStringField(
			"Settings.Input-listener.Cancel-phrase", "cancel");

	@PandaInject
	private static PandaInputListenerService pandaInputListenerService;

	private @Getter UUID playerUuid;
	private @Getter Callback<T> callback;
	private @Getter @Setter boolean cancellable = true;
	private @Getter boolean cancelled = false;

	public InputListener(UUID playerUuid, Callback<T> callback) {
		this.playerUuid = playerUuid;
		this.callback = callback;
	}

	public void cancel() {
		if (this.callback != null) {
			this.callback.cancel();
		}

		this.cancelled = true;

		try {
			pandaInputListenerService.unregisterListener(playerUuid);
		} catch (PlayerNotListenedException e) {
			// Ignore
		}
	}

	public void onProcessStartEvent() {
	}

	public void onMessageEvent(AsyncPlayerChatEvent event) {
		if (SETTINGS_INPUTLISTENER_CANCELPHRASE.hasContent() && SETTINGS_INPUTLISTENER_CANCELPHRASE.getContent()
				.toLowerCase().equals(event.getMessage().toLowerCase())) {
			event.setCancelled(true);
			callback.cancel();
			return;
		}
	}

	public void onInteractEvent(PlayerInteractEvent event) {
	}

	public void onInventoryInteractEvent(InventoryInteractEvent e) {
	}

	public void onInventoryCloseEvent(InventoryCloseEvent e) {
	}

	public void onInventoryClickEvent(InventoryClickEvent e) {
		this.onInventoryInteractEvent(e);
	}

	public void onInventoryDragEvent(InventoryDragEvent e) {
		this.onInventoryInteractEvent(e);
	}

	public interface Callback<T> {

		public abstract boolean input(T value);

		public default void cancel() {
		}

	}

}
