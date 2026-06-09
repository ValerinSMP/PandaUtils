package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.PandaAbstractPlaceholderAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesStartupService;

public class InventoryItemBuilder extends ItemBuilder {

	@PandaInject
	private static PandaAbstractPlaceholderAPI<?> placeholderApi;

	private Player player;

	public InventoryItemBuilder(Player player) {
		super();
		this.player = player;
	}

	@Override
	public ItemStack apply(ItemStack item) {
		if (PandaInventoriesStartupService.INVENTORIES_PLACEHOLDERAPISUPPORT.isTrue() && placeholderApi != null
				&& placeholderApi.isHooked()) {
			String originalName = this.getName();
			List<String> originalLore = this.getLore();

			if (originalName != null && !originalName.isEmpty()) {
				setName(placeholderApi.getHook().applyPlaceholders(originalName, player));
			}

			if (originalLore != null && !originalLore.isEmpty()) {
				setLore(placeholderApi.getHook().applyPlaceholders(originalLore, player));
			}

			item = super.apply(item);

			setName(originalName);
			setLore(originalLore);

			return item;
		} else {
			return super.apply(item);
		}
	}

	public static InventoryItemBuilder inst(Player player) {
		return new InventoryItemBuilder(player);
	}

}
