package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.inventory.ItemStack;

import lombok.Getter;
import lombok.Setter;

public class PandaInventoryItem {

	public static enum ClickResult {
		CANCEL, IGNORE
	}

	// Constants

	public static final String ITEMS_KEY = "Items";
	public static final String DATA_KEY = "Data";
	public static final String SLOTS_KEY = "Slots";

	public static final String DISPLAY_ITEM_KEY = "Display-item";
	public static final String PERMISSION_KEY = "Permission";
	public static final String PLAYER_COMMANDS_KEY = "Player-commands";
	public static final String CONSOLE_COMMANDS_KEY = "Console-commands";
	public static final String CLOSE_INVENTORY_ON_CLICK_KEY = "Close-inventory-on-click";
	public static final String UPDATE_EVERY_X_TICKS_KEY = "Update-every-X-ticks";
	public static final String APPLY_PLACEHOLDERAPI_KEY = "Apply-PlaceholderAPI";

	private @Getter @Setter ClickResult defaultClickResult = ClickResult.CANCEL;

	private Map<String, ItemStack> items = new HashMap<>();
	private Map<String, Object> data = new HashMap<>();
	private int[] slots = new int[0];

	private @Getter @Setter String permission;

	private @Getter List<String> playerCommands = new ArrayList<>();
	private @Getter List<String> consoleCommands = new ArrayList<>();

	private @Getter @Setter boolean closeInventoryOnClick = false;
	private @Getter @Setter long updateEveryXTicks = 0L;
	private @Getter @Setter boolean applyPlaceholderAPI = false;

	public ItemStack getDisplayItem() {
		return this.getItem(DISPLAY_ITEM_KEY);
	}

	public ItemStack getItem(String id) {
		return this.getItem(id.toLowerCase(), null);
	}

	public ItemStack getItem(String id, ItemStack defaultItem) {
		return this.items.getOrDefault(id, defaultItem);
	}

	public void setItem(String id, ItemStack defaultItem) {
		this.items.put(id, defaultItem);
	}

	public <T> T getData(String id) {
		return this.getData(id, null);
	}

	@SuppressWarnings("unchecked")
	public <T> T getData(String id, T defaultObject) {
		return (T) this.data.getOrDefault(id, defaultObject);
	}

	public void setData(String id, Object object) {
		this.data.put(id, object);
	}

	public void setSlots(int... slots) {
		this.slots = slots;
	}

	public int[] getSlots() {
		return Arrays.copyOf(slots, slots.length);
	}

}