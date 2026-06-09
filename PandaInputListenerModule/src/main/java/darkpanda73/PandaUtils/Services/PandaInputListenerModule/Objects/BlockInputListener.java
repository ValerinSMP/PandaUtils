package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects;

import java.util.UUID;

import org.bukkit.block.Block;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlockInputListener extends InputListener<Block> {

	public BlockInputListener(UUID playerUuid, Callback<Block> callback) {
		super(playerUuid, callback);
	}

	@Override
	public void onInteractEvent(PlayerInteractEvent event) {
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && this.getCallback().input(event.getClickedBlock())) {
			this.cancel();
		}
	}

}
