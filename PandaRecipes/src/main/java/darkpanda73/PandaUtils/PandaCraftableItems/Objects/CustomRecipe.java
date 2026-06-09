package darkpanda73.PandaUtils.PandaCraftableItems.Objects;

import java.util.Collections;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.Plugin;

import darkpanda73.PandaUtils.PandaCraftableItems.Utils.RecipeUtilities;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Setter(lombok.AccessLevel.NONE)
public abstract class CustomRecipe {

	protected ItemStack[] recipe = new ItemStack[9];
	protected ShapedRecipe minecraftRecipe;

	public void unloadRecipe() {
		RecipeUtilities.unloadRecipes(Collections.singleton(this));
	}

	public void loadRecipe() {
		if (hasRecipe()) {
			try {
				Class<?> nk = Class.forName("org.bukkit.NamespacedKey");
				this.minecraftRecipe = ShapedRecipe.class.getConstructor(nk, ItemStack.class).newInstance(
						nk.getConstructor(Plugin.class, String.class).newInstance(getPlugin(), getRecipeId()),
						getResult());
			} catch (Exception e) {
				e.printStackTrace();
				try {
					this.minecraftRecipe = ShapedRecipe.class.getConstructor(ItemStack.class).newInstance(getResult());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}

			ItemStack[][] itemStacksShape = RecipeUtilities.toShape(recipe);
			String[] shape = new String[itemStacksShape.length];

			char currentChar = 97;
			for (int i = 0; i < itemStacksShape.length; i++) {
				shape[i] = "";
				for (int j = 0; j < itemStacksShape[i].length; j++) {
					if (itemStacksShape[i][j] != null) {
						shape[i] += currentChar++;
					} else {
						shape[i] += ' ';
					}
				}
			}

			minecraftRecipe.shape(shape);

			for (int i = 0; i < itemStacksShape.length; i++) {
				for (int j = 0; j < itemStacksShape[i].length; j++) {
					ItemStack item = itemStacksShape[i][j];
					if (item != null) {
						minecraftRecipe.setIngredient(shape[i].charAt(j), item.getType());
					}
				}
			}

			Bukkit.addRecipe(minecraftRecipe);
			for (Player pl : Bukkit.getOnlinePlayers()) {
				darkpanda73.PandaUtils.PandaUtilities.Recipes.RecipeUtilities.discoverRecipe(pl, minecraftRecipe);
			}
		} else {
			minecraftRecipe = null;
		}
	}

	public void refreshRecipe() {
		if (this.minecraftRecipe != null) {
			this.unloadRecipe();
			this.loadRecipe();
		}
	}

	public boolean hasRecipe() {
		for (ItemStack i : recipe) {
			if (i != null && i.getType() != Material.AIR) {
				return true;
			}
		}
		return false;
	}

	public boolean isItem(ItemStack itemstack) {
		return getItemStack().isSimilar(itemstack);
	}

	public ItemStack getResult(ItemStack[] recipe) {
		ItemStack[][] recipeShape = RecipeUtilities.toShape(recipe);
		ItemStack[][] customRecipeShape = RecipeUtilities.toShape(this.recipe);

		if (recipeShape.length != customRecipeShape.length) {
			return null;
		}

		for (int i = 0; i < recipeShape.length; i++) {
			if (recipeShape[i].length != customRecipeShape[i].length) {
				return null;
			}

			for (int j = 0; j < recipeShape[i].length; j++) {
				ItemStack it1 = recipeShape[i][j];
				ItemStack it2 = customRecipeShape[i][j];

				if (it1 == null && it2 == null) {
					continue;
				}

				if (ItemStackUtilities.isSimilar(it1, it2)) {
					continue;
				}

				return null;
			}
		}
		return getResult();
	}

	public abstract PandaPluginClass getPlugin();

	public abstract String getRecipeId();

	public abstract ItemStack getItemStack();

	public abstract ItemStack getResult();

}
