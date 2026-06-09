package darkpanda73.PandaUtils.PandaCraftableItems;

import java.util.HashMap;
import java.util.List;

import org.bukkit.inventory.ShapedRecipe;

import darkpanda73.PandaUtils.PandaCraftableItems.Objects.CustomRecipe;
import darkpanda73.PandaUtils.PandaCraftableItems.Utils.RecipeUtilities;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;

@PandaService
public abstract class CustomRecipesService<T extends CustomRecipe> {

	protected HashMap<String, T> customRecipes = new HashMap<>();

	@LoadMethod
	private void load() {
		this.getCustomRecipes().forEach(recipe -> {
			if (!this.customRecipes.containsKey(recipe.getRecipeId())) {
				this.customRecipes.put(recipe.getRecipeId(), recipe);
				recipe.loadRecipe();
			}
		});
	}

	@UnloadMethod
	private void unload() {
		RecipeUtilities.unloadRecipes(this.customRecipes.values());
		this.customRecipes.clear();
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

	public T getRecipeById(String id) {
		return customRecipes.get(id);
	}

	public T findRecipe(ShapedRecipe recipe) {
		return this.customRecipes.values().stream()
				.filter(customRecipe -> RecipeUtilities.isSameRecipe(recipe, customRecipe.getMinecraftRecipe()))
				.findFirst().orElse(null);
	}

	public void registerRecipe(T recipe) {
		unregisterRecipe(recipe);
		this.customRecipes.put(recipe.getRecipeId(), recipe);
		recipe.loadRecipe();
	}

	public void unregisterRecipe(T recipe) {
		CustomRecipe customRecipe = this.customRecipes.remove(recipe.getRecipeId());
		if (customRecipe != null) {
			customRecipe.unloadRecipe();
		}
	}

	public abstract List<T> getCustomRecipes();

}