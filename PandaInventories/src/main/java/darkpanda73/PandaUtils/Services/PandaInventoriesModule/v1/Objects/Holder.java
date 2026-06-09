package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import lombok.Getter;

public abstract class Holder implements InventoryHolder {

	private @Getter Player player = null;
	private @Getter Inventory previousInventory = null;

	public Holder(Player pl) {
		this(pl.getOpenInventory() != null && pl.getOpenInventory().getTopInventory() != null
				&& pl.getOpenInventory().getTopInventory().getHolder() != pl.getInventory().getHolder()
						? pl.getOpenInventory().getTopInventory()
						: null,
				pl);
	}

	public Holder(Inventory previousInventory, Player player) {
		this.previousInventory = previousInventory;
		this.player = player;
	}

	public void openInventory() {
		Inventory result = getInventory();
		if (result != null) {
			TasksUtils.execute(() -> player.openInventory(getInventory()));
		} else {
			closeInventory();
		}
	}

	public void closeInventory() {
		TasksUtils.execute(() -> {
			player.closeInventory();
			player.updateInventory();
		});
	}

	public void goToPreviousInventory() {
		Inventory inv = getPreviousInventory();
		setPreviousInventory(null);
		if (inv != null && inv.getHolder() != this) {
			if (inv.getHolder() instanceof Holder) {
				((Holder) inv.getHolder()).updateInventory();
			} else {
				TasksUtils.execute(() -> player.openInventory(inv));
			}
		} else {
			closeInventory();
		}
	}

	public Holder setPreviousInventory(Inventory previnv) {
		this.previousInventory = previnv;
		return this;
	}

	protected abstract String getTitle();

	public abstract boolean updateInventory(); 
}
