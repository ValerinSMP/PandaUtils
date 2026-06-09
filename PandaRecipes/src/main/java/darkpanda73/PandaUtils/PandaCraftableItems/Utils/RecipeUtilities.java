package darkpanda73.PandaUtils.PandaCraftableItems.Utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;

import darkpanda73.PandaUtils.PandaCraftableItems.Objects.CustomRecipe;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder;
import darkpanda73.PandaUtils.Utilities.Java.Classes.ClassFinder.ClassResult;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

public class RecipeUtilities {

	private static final ClassResult NAMESPACEDKEY_CLASS;
	private static final MethodResult SHAPEDRECIPE_GETKEY_METHOD;
	private static final MethodResult BUKKIT_REMOVERECIPE_METHOD;

	static {
		NAMESPACEDKEY_CLASS = ClassFinder.forClass("org.bukkit.NamespacedKey").findSafely();
		SHAPEDRECIPE_GETKEY_METHOD = MethodFinder.forData(ShapedRecipe.class, "getKey").findSafely();
		BUKKIT_REMOVERECIPE_METHOD = NAMESPACEDKEY_CLASS.isPresent()
				? MethodFinder.forData(Bukkit.class, "removeRecipe", NAMESPACEDKEY_CLASS.getFoundClass()).findSafely()
				: null;
	}

	public static <T extends CustomRecipe> void unloadRecipes(Collection<T> customRecipes) {
		if (!customRecipes.isEmpty()) {
			if (BUKKIT_REMOVERECIPE_METHOD != null && BUKKIT_REMOVERECIPE_METHOD.isPresent()) {
				customRecipes.forEach(customRecipe -> BUKKIT_REMOVERECIPE_METHOD.invokeSafelyWithStackTrace(null,
						getNamepsacedKey(customRecipe.getMinecraftRecipe())));
			} else {
				customRecipes.forEach(customRecipe -> {
					if (customRecipe.getMinecraftRecipe() != null) {
						for (Player pl : Bukkit.getOnlinePlayers()) {
							darkpanda73.PandaUtils.PandaUtilities.Recipes.RecipeUtilities.undiscoverRecipe(pl,
									customRecipe.getMinecraftRecipe());
						}
					}
				});

				List<Recipe> recipes = new ArrayList<>();
				Iterator<Recipe> it = Bukkit.recipeIterator();

				while (it.hasNext()) {
					Recipe recipe = it.next();
					if (recipe instanceof ShapedRecipe) {
						if (customRecipes.stream().anyMatch(customRecipe -> RecipeUtilities
								.isSameRecipe((ShapedRecipe) recipe, customRecipe.getMinecraftRecipe()))) {
							continue;
						}
					}
					recipes.add(recipe);
				}

				Bukkit.clearRecipes();
				recipes.forEach(Bukkit::addRecipe);
			}
		}
	}

	public static ItemStack[][] toShape(ItemStack[] itemStacks) {
		Pair<Integer, Integer> minPoint = null;
		Pair<Integer, Integer> maxPoint = null;

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				if (itemStacks[(i * 3) + j] != null) {
					if (minPoint == null) {
						minPoint = Pair.of(i, j);
					} else {
						if (minPoint.getFirst() > i) {
							minPoint.setFirst(i);
						}
						if (minPoint.getSecond() > j) {
							minPoint.setSecond(j);
						}
					}

					if (maxPoint == null) {
						maxPoint = Pair.of(i, j);
					} else {
						if (maxPoint.getFirst() < i) {
							maxPoint.setFirst(i);
						}
						if (maxPoint.getSecond() < j) {
							maxPoint.setSecond(j);
						}
					}
				}
			}
		}

		ItemStack[][] shape = new ItemStack[(maxPoint.getFirst() + 1) - minPoint.getFirst()][(maxPoint.getSecond() + 1)
				- minPoint.getSecond()];

		for (int i = 0; i < shape.length; i++) {
			for (int j = 0; j < shape[i].length; j++) {
				shape[i][j] = itemStacks[((i + minPoint.getFirst()) * 3) + j + (minPoint.getSecond())];
			}
		}

		return shape;
	}

	public static boolean isSameRecipe(ShapedRecipe recipe1, ShapedRecipe recipe2) {
		Boolean hasSameKey = hasSameKey(recipe1, recipe2);

		if (hasSameKey != null) {
			return hasSameKey;
		}

		if (recipe1.getResult().isSimilar(recipe2.getResult())) {
			for (int i = 0; i < recipe1.getShape().length && i < recipe2.getShape().length; i++) {
				char[] shape1 = recipe1.getShape()[i].toCharArray();
				char[] shape2 = recipe2.getShape()[i].toCharArray();
				for (int j = 0; j < shape1.length && j < shape2.length; j++) {
					ItemStack i1 = recipe1.getIngredientMap().get(shape1[j]);
					ItemStack i2 = recipe2.getIngredientMap().get(shape2[j]);
					if (i1 == null ? i2 != null : !i1.isSimilar(i2)) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public static boolean isSameRecipe(ItemStack[] recipe1, ItemStack[] recipe2) {
		ItemStack[][] recipeShape = RecipeUtilities.toShape(recipe1);
		ItemStack[][] customRecipeShape = RecipeUtilities.toShape(recipe2);

		if (recipeShape.length != customRecipeShape.length) {
			return false;
		}

		for (int i = 0; i < recipeShape.length; i++) {
			if (recipeShape[i].length != customRecipeShape[i].length) {
				return false;
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

				return false;
			}
		}

		return true;
	}

	public static Boolean hasSameKey(Recipe recipe1, Recipe recipe2) {
		return SHAPEDRECIPE_GETKEY_METHOD != null ? getKey(recipe1).equals(getKey(recipe2)) : null;
	}

	public static Object getNamepsacedKey(Recipe recipe) {
		return SHAPEDRECIPE_GETKEY_METHOD.invokeSafely(recipe);
	}

	public static String getKey(Recipe recipe) {
		return getNamepsacedKey(recipe).toString();
	}

}
