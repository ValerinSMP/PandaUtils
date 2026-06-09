package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.PandaAbstractPlaceholderAPI;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.Holder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.ItemUtilities;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Accessors(chain = true)
@RequiredArgsConstructor
public class Item {

	@PandaInject
	private static PandaAbstractPlaceholderAPI<?> placeholderApi;

	public static enum ClickResult {
		CANCEL, IGNORE
	}

	// Constants
	public static final String SLOTS_KEY = "Slots";
	public static final String DISPLAYITEM_KEY = "Display-item";
	public static final String PERMISSION_KEY = "Permission";
	public static final String COMMANDS_KEY = "Commands";
	public static final String CONSOLECOMMANDS_KEY = "Console-commands";
	public static final String CLOSEINVENTORYONCLICK_KEY = "Close-inventory-on-click";

	private @Getter @NonNull @EqualsAndHashCode.Include String id;
	private @Getter @NonNull Map<String, Object> data;
	private @Getter Map<String, ItemStack> items = new LinkedHashMap<>();
	private @Getter int[] slots = new int[0];
	private @Getter String permission = null;
	private @Getter @Setter ClickResult defaultClickResult = ClickResult.CANCEL;

	private @Setter Method pregenerateItemMethod;
	private @Setter Method generateItemMethod;
	private @Setter Method onClickItemMethod;

	private List<String> commands;
	private List<String> consoleCommands;

	private boolean closeInventoryOnClick;

	@SuppressWarnings("unchecked")
	public void loadItemData() {
		items.clear();
		slots = ItemUtilities.stringToSlots(this.data.get(SLOTS_KEY).toString());
		permission = this.data.containsKey(PERMISSION_KEY) ? this.data.get(PERMISSION_KEY).toString() : null;
		commands = this.data.containsKey(COMMANDS_KEY) ? (List<String>) this.data.get(COMMANDS_KEY)
				: Collections.emptyList();
		consoleCommands = this.data.containsKey(CONSOLECOMMANDS_KEY) ? (List<String>) this.data.get(CONSOLECOMMANDS_KEY)
				: Collections.emptyList();
		closeInventoryOnClick = this.data.containsKey(CLOSEINVENTORYONCLICK_KEY)
				? (Boolean) this.data.get(CLOSEINVENTORYONCLICK_KEY)
				: false;

		if (pregenerateItemMethod != null) {
			try {
				JavaUtils.executeMethod(null, this.pregenerateItemMethod, this);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				this.items.clear();
			}
		} else {
			try {
				this.items.put(DISPLAYITEM_KEY, ItemUtilities.itemBySection(this, DISPLAYITEM_KEY));
			} catch (Exception e) {
			}
		}
	}

	public Object generateItem(Holder inventory, Object... params) {
		if (this.generateItemMethod != null) {
			try {
				int newLength = this.generateItemMethod.getParameterCount();
				Object[] newParams = new Object[newLength];

				for (int i = 1; i < newLength && i - 1 < params.length; i++) {
					newParams[i] = params[i - 1];
				}

				if (newLength > 0) {
					newParams[0] = this;
				}

				return JavaUtils.executeMethod(inventory, this.generateItemMethod, newParams);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return items.get(DISPLAYITEM_KEY);
		}
	}

	public ClickResult onClickItem(Holder inventory, Object... params) {
		if (this.onClickItemMethod != null) {
			try {
				int newLength = this.onClickItemMethod.getParameterCount();
				Object[] newParams = new Object[newLength];

				for (int i = 1; i < newLength && i - 1 < params.length; i++) {
					newParams[i] = params[i - 1];
				}

				if (newLength > 0) {
					newParams[0] = this;
				}

				Object result = JavaUtils.executeMethod(inventory, this.onClickItemMethod, newParams);

				return result != null && result instanceof ClickResult ? (ClickResult) result : defaultClickResult;
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e1) {
				e1.printStackTrace();
			}
		}

		if (this.commands != null && !this.commands.isEmpty()) {
			TasksUtils.execute(() -> this.commands.forEach(command -> {
				if (placeholderApi != null && placeholderApi.isHooked()) {
					Bukkit.dispatchCommand(inventory.getPlayer(),
							placeholderApi.getHook().applyPlaceholders(command, inventory.getPlayer()));
				} else {
					Bukkit.dispatchCommand(inventory.getPlayer(), command);
				}
			}));
		}

		if (this.consoleCommands != null && !this.consoleCommands.isEmpty()) {
			TasksUtils.execute(() -> this.consoleCommands.forEach(command -> {
				if (placeholderApi != null && placeholderApi.isHooked()) {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
							placeholderApi.getHook().applyPlaceholders(command, inventory.getPlayer()));
				} else {
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
				}
			}));
		}

		if (closeInventoryOnClick) {
			TasksUtils.execute(() -> inventory.closeInventory());
		}

		return defaultClickResult;
	}

}
