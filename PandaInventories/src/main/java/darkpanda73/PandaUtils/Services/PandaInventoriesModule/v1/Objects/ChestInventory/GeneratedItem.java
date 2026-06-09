package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.InventoryItemBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class GeneratedItem {

	private @Getter Item item;
	private @Getter ItemStack itemStack;
	private @Getter ItemBuilder itemBuilder;

	public GeneratedItem(Item item, ItemStack itemStack, Player player) {
		this(item, itemStack, itemStack != null ? InventoryItemBuilder.inst(player).fromItem(itemStack) : null);
	}

	public GeneratedItem(Item item, ItemBuilder itemBuilder) {
		this(item, null, itemBuilder);
	}

	public ItemStack generate() {
		if (this.itemBuilder != null) {
			this.itemStack = itemBuilder.build();
		}
		return this.itemStack;
	}

}
