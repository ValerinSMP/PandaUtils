package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import lombok.Getter;

public class AnvilMessageInputListener extends InputListener<String> {

	private @Getter AnvilHolder anvilHolder;

	public AnvilMessageInputListener(UUID playerUuid, Callback<String> callback, String anvilTitle) {
		super(playerUuid, callback);

		this.anvilHolder = new AnvilHolder(anvilTitle);
	}

	@Override
	public void onProcessStartEvent() {
		this.anvilHolder.openInventory(Bukkit.getPlayer(this.getPlayerUuid()));
	}

	@Override
	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		if (e.getInventory() != null && e.getInventory().getHolder() == this.anvilHolder) {
			this.cancel();
		}
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

	public interface AnvilCallback extends Callback<String> {

		public void unfinishedInput(String input);

	}

	public static class AnvilHolder implements InventoryHolder {

		private @Getter String title = null;
		private Inventory inventory = null;

		public AnvilHolder(String title) {
			this.title = title;
		}

		public void openInventory(Player player) {
			TasksUtils.execute(() -> player.openInventory(getInventory()));
		}

		@Override
		public Inventory getInventory() {
			if (inventory == null) {
				try {
					this.inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getTitle());
				} catch (IllegalArgumentException e) {
					if (e.getMessage().equals("Title cannot be longer than 32 characters")) {
						this.inventory = Bukkit.createInventory(this, InventoryType.ANVIL, getTitle().substring(0, 32));
					} else {
						throw e;
					}
				}

				this.inventory.setContents(new ItemStack[] {
						ItemBuilder.inst().setMaterial("RED_STAINED_GLASS_PANE").setTooltip(false).build(),
						ItemBuilder.inst().setMaterial("LIME_STAINED_GLASS_PANE").setTooltip(false).build(),
						ItemBuilder.inst().setMaterial("NAME_TAG").setName("&7&o---").build() });
			}

			return inventory;
		}
	}

}
