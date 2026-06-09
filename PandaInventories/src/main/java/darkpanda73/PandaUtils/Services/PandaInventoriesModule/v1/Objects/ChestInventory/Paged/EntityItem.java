package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import lombok.Getter;

public class EntityItem<E> extends GeneratedItem {

	private @Getter E entity;
	private @Getter int index;

	public EntityItem(Item item, ItemStack itemStack, Player player, E entity, int index) {
		super(item, itemStack, player);
		this.entity = entity;
		this.index = index;
	}

}
