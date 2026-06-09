package darkpanda73.PandaUtils.PandaCraftableItems.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ShapedRecipe;

import darkpanda73.PandaUtils.PandaCraftableItems.CustomRecipesService;
import darkpanda73.PandaUtils.PandaCraftableItems.Objects.CustomRecipe;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;

@PandaListener
public abstract class CustomRecipesListener<T extends CustomRecipe> implements Listener {

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCraft(PrepareItemCraftEvent e) {
		if (e.getRecipe() != null && e.getRecipe() instanceof ShapedRecipe) {
			T customRecipe = getCustomRecipeService().findRecipe((ShapedRecipe) e.getRecipe());
			if (customRecipe != null && this.canCraft((Player) e.getView().getPlayer(), customRecipe)) {
				e.getInventory().setResult(customRecipe.getResult(e.getInventory().getMatrix()));
			}
		}
	}

	public boolean canCraft(Player player, T customRecipe) {
		return true;
	}

	public abstract CustomRecipesService<T> getCustomRecipeService();

}
