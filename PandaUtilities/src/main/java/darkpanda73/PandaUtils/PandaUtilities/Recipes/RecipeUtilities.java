package darkpanda73.PandaUtils.PandaUtilities.Recipes;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ShapedRecipe;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;

public class RecipeUtilities {

	private static MethodResult DISCOVER_RECIPE;
	private static MethodResult UNDISCOVER_RECIPE;
	private static MethodResult GET_KEY;

	static {
		try {
			GET_KEY = MethodFinder.forClass(ShapedRecipe.class).setName("getKey").find();
			DISCOVER_RECIPE = MethodFinder.forClass(Player.class).setName("discoverRecipe").find();
			UNDISCOVER_RECIPE = MethodFinder.forClass(Player.class).setName("undiscoverRecipe").find();
		} catch (Exception e) {
			GET_KEY = null;
			DISCOVER_RECIPE = null;
			UNDISCOVER_RECIPE = null;

			PandaPluginClass.getSimpleLogger().sendWarning(
					"Unable to find methods for recipe discovery. Omitting discovery and advanced recipes implementation!");
		}
	}

	public static boolean discoverRecipe(Player player, ShapedRecipe namespace) {
		try {
			if (GET_KEY != null && DISCOVER_RECIPE != null)
				return (boolean) DISCOVER_RECIPE.invoke(player, GET_KEY.invoke(namespace));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean undiscoverRecipe(Player player, ShapedRecipe namespace) {
		try {
			if (GET_KEY != null && UNDISCOVER_RECIPE != null)
				return (boolean) UNDISCOVER_RECIPE.invoke(player, GET_KEY.invoke(namespace));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

}
