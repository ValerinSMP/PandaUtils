package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults;

import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;

public abstract class DefaultConfirmationInventory extends ChestInventoryObject {

	private Runnable action;
	private boolean previousOnAction;

	public DefaultConfirmationInventory(Player player, Runnable action) {
		this(player, action, true);
	}

	public DefaultConfirmationInventory(Player player, Runnable action, boolean previousOnAction) {
		super(player);

		this.action = action;
		this.previousOnAction = previousOnAction;
	}

	@ItemExecutor("Confirm-button")
	public void onClickConfirmButton() {
		if (action != null) {
			action.run();
		}

		if (previousOnAction) {
			goToPreviousInventory();
		}
	}

	@ItemExecutor("Cancel-button")
	public void onClickCancelButton() {
		goToPreviousInventory();
	}

}
