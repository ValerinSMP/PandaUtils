package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils;

import java.util.function.Function;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.Holder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;

public class InventoryUtilities {

	public static <T extends Holder> void forceUpdate(Class<T> holderToUpdate) {
		Bukkit.getOnlinePlayers().forEach(player -> forceUpdate(holderToUpdate, player));
	}

	public static <T extends Holder> void forceUpdate(Class<T> holderToUpdate, Function<T, Boolean> checkConditions) {
		Bukkit.getOnlinePlayers().forEach(player -> forceUpdate(holderToUpdate, player, checkConditions));
	}

	public static <T extends Holder> void forceUpdate(Class<T> holderToUpdate, Player playerToUpdate) {
		forceUpdate(holderToUpdate, playerToUpdate, (inv) -> true);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Holder> void forceUpdate(Class<T> holderToUpdate, Player playerToUpdate,
			Function<T, Boolean> checkConditions) {
		if (playerToUpdate != null && playerToUpdate.isOnline()
				&& darkpanda73.PandaUtils.PandaUtilities.ItemStack.InventoryUtilities
						.getTopInventory(playerToUpdate) != null) {
			InventoryHolder holder = darkpanda73.PandaUtils.PandaUtilities.ItemStack.InventoryUtilities
					.getTopInventory(playerToUpdate).getHolder();
			if (holder != null && holderToUpdate.isAssignableFrom(holder.getClass())
					&& checkConditions.apply((T) holder)) {
				if (PagedChestInventoryObject.class.isAssignableFrom(holder.getClass())) {
					((PagedChestInventoryObject<?>) holder).updateEntityList();
				}
				((T) holder).updateInventory();
			}
		}
	}

}
