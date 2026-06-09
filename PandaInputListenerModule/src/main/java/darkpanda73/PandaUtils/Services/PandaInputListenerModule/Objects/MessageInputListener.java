package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects;

import java.util.UUID;

import org.bukkit.event.player.AsyncPlayerChatEvent;

public class MessageInputListener extends InputListener<String> {

	public MessageInputListener(UUID playerUuid, Callback<String> callback) {
		super(playerUuid, callback);
	}

	@Override
	public void onMessageEvent(AsyncPlayerChatEvent e) {
		super.onMessageEvent(e);

		if (!this.isCancelled()) {
			e.setCancelled(true);
			if (this.getCallback().input(e.getMessage())) {
				this.cancel();
			}
		}
	}

}
