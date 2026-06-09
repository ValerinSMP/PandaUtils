package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.inventory.InventoryType;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.Holder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;

@PandaListener
public class PandaInventoryEvents implements Listener {

	@PandaInject
	private PandaPluginClass plugin;

	@EventHandler
	public void onInvClick(InventoryClickEvent e) {
		if (e.getInventory() != null && !e.getInventory().getViewers().isEmpty()
				&& e.getInventory().getType() == InventoryType.CHEST && e.getInventory().getHolder() != null
				&& Holder.class.isAssignableFrom(e.getInventory().getHolder().getClass())) {
			((ChestInventoryObject) e.getInventory().getHolder()).onClick(e);
		}
	}

	@EventHandler
	public void onInvDrag(InventoryDragEvent e) {
		if (e.getInventory() != null && !e.getInventory().getViewers().isEmpty()
				&& e.getInventory().getType() == InventoryType.CHEST && e.getInventory().getHolder() != null
				&& Holder.class.isAssignableFrom(e.getInventory().getHolder().getClass())) {
			((ChestInventoryObject) e.getInventory().getHolder()).onDrag(e);
		}
	}

	@EventHandler
	public void onInvClose(InventoryCloseEvent e) {
		if (e.getInventory() != null && !e.getInventory().getViewers().isEmpty()
				&& e.getInventory().getType() == InventoryType.CHEST && e.getInventory().getHolder() != null
				&& Holder.class.isAssignableFrom(e.getInventory().getHolder().getClass())) {
			((ChestInventoryObject) e.getInventory().getHolder()).onClose(e);
		}
	}

	@EventHandler
	public void onInvMoveItem(InventoryMoveItemEvent e) {
		if (e.getDestination() != null && !e.getDestination().getViewers().isEmpty()
				&& e.getDestination().getType() == InventoryType.CHEST && e.getDestination().getHolder() != null
				&& Holder.class.isAssignableFrom(e.getDestination().getHolder().getClass())) {
			e.setCancelled(true);
		}
	}

}