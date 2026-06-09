package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.PlaceholderAPI.PandaAbstractPlaceholderAPI;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.InventoryUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PandaInventoriesStartupService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.PlayerHeadCacheService;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.Holder;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item.ClickResult;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder.MethodResult;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;

public abstract class ChestInventoryObject extends Holder {

	private static MethodResult INVENTORY_VIEW_SET_TITLE_METHOD;
	private static boolean initialized = false;

	private static void initialize(Object object) {
		if (!initialized && object instanceof InventoryView) {
			INVENTORY_VIEW_SET_TITLE_METHOD = MethodFinder.forClass(object.getClass()).setName("setTitle")
					.setParams(String.class).findSafely();
			initialized = true;
		}
	}

	@PandaInject
	private static PandaInventoriesService inventoryService;

	@PandaInject
	private static PlayerHeadCacheService playerHeadCacheService;

	@PandaInject
	private static PandaAbstractPlaceholderAPI<?> placeholderApi;

	private @Getter ChestInventoryData chestInventoryData = null;

	private @Getter Replacement[] replacements = new Replacement[0];

	private @Getter Replacement[] titleReplacements = new Replacement[0];

	private HashMap<Integer, GeneratedItem> slots = new HashMap<>();

	private Inventory inventory = null;

	private List<Pair<ItemStack, FutureTask<ItemStack>>> pendingPlayerHeads = new ArrayList<>();

	public ChestInventoryObject(Player player) {
		this(player, null);
	}

	public ChestInventoryObject(Player player, ChestInventoryData chestInventoryData) {
		super(player);

		this.chestInventoryData = chestInventoryData != null ? chestInventoryData
				: inventoryService.findData(this.getClass());
	}

	protected String getTitle() {
		return MessageTemplate.inst(
				placeholderApi.isHooked() && PandaInventoriesStartupService.INVENTORIES_PLACEHOLDERAPISUPPORT.isTrue()
						? placeholderApi.getHook().applyPlaceholders(getUnformattedTitle(), getPlayer())
						: getUnformattedTitle())
				.setReplacements(getTitleReplacements()).toString();
	}

	protected String getUnformattedTitle() {
		return this.chestInventoryData.getTitle();
	}

	public void setReplacements(Replacement... replacements) {
		this.replacements = replacements;
	}

	public void setTitleReplacements(Replacement... titleReplacements) {
		this.titleReplacements = titleReplacements;
	}

	protected int getSize() {
		return this.chestInventoryData.getSize();
	}

	public void setSlot(int slot, GeneratedItem generatedItem) {
		this.slots.put(slot, generatedItem);
	}

	@Override
	public Inventory getInventory() {
		if (inventory == null && chestInventoryData.getSize() > 0) {
			Inventory inventory;

			try {
				inventory = Bukkit.createInventory(this, this.chestInventoryData.getSize(), getTitle());
			} catch (IllegalArgumentException e) {
				if (e.getMessage().equals("Title cannot be longer than 32 characters")) {
					inventory = Bukkit.createInventory(this, this.chestInventoryData.getSize(),
							getTitle().substring(0, 32));
				} else {
					throw e;
				}
			}

			final Inventory finalInventory = inventory;

			this.inventory = finalInventory;
		}

		if (inventory != null) {
			updateContent();
		}

		return inventory;
	}

	@Override
	public void openInventory() {
		Inventory result = getInventory();
		if (result != null) {
			TasksUtils.execute(() -> {
				getPlayer().openInventory(result);

				updateTitleIfChanged(getPlayer(), true);
			});
		} else {
			closeInventory();
		}
	}

	public final void updateTitleIfChanged(Player player) {
		this.updateTitleIfChanged(player, false);
	}

	public final void updateTitleIfChanged(Player player, boolean force) {
		Inventory topInventory = InventoryUtilities.getTopInventory(player);
		if (topInventory != null && topInventory.getHolder() == this) {
			if (!initialized) {
				initialize(InventoryUtilities.getOpenInventory(getPlayer()));
			}

			if (INVENTORY_VIEW_SET_TITLE_METHOD != null && INVENTORY_VIEW_SET_TITLE_METHOD.isPresent()
					&& (force || !getTitle().equals(InventoryUtilities.getTitle(player)))) {
				try {
					INVENTORY_VIEW_SET_TITLE_METHOD.invoke(InventoryUtilities.getOpenInventory(player), getTitle());
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public final boolean updateInventory() {
		Inventory result = getInventory();
		if (result != null) {
			Inventory inv = InventoryUtilities.getTopInventory(getPlayer());
			if (inv == null || inv.getHolder() != this) {
				TasksUtils.execute(() -> {
					getPlayer().openInventory(result);
					updateTitleIfChanged(getPlayer(), true);
				});
			} else {
				TasksUtils.execute(() -> {
					updateTitleIfChanged(getPlayer());
				});
			}
			return true;
		} else {
			closeInventory();
		}
		return false;
	}

	public final void updateContent() {
		slots.clear();

		onPreUpdate();

		List<GeneratedItem> generatedItems = this.chestInventoryData.getItems().stream()
				.filter(item -> item.getSlots().length > 0).map(item -> {
					if (item.getPermission() != null && !item.getPermission().isEmpty()
							&& !getPlayer().hasPermission(item.getPermission())) {
						return null;
					}

					Object object = item.generateItem(this, this.slots.values().stream()
							.filter(generatedItem -> generatedItem.getItem() == item).findFirst().orElse(null));

					if (object != null) {
						if (object instanceof GeneratedItem) {
							return (GeneratedItem) object;
						} else if (object instanceof ItemBuilder) {
							return new GeneratedItem(item, (ItemBuilder) object);
						} else if (object instanceof ItemStack) {
							return new GeneratedItem(item, (ItemStack) object, getPlayer());
						}
					}

					return null;
				}).filter(Objects::nonNull).collect(Collectors.toList());

		this.slots.clear();

		generatedItems.forEach(generatedItem -> {
			for (int slot : generatedItem.getItem().getSlots()) {
				if (slot >= 0 && slot < getSize()) {
					this.setSlot(slot, generatedItem);
				}
			}
		});

		onPostUpdate();

		if (this.inventory != null) {
			this.inventory.clear();

			Replacement[] replacements = getReplacements();

			this.slots.values().stream().collect(Collectors.toSet()).parallelStream().forEach(generatedItem -> {
				if (generatedItem.getItemBuilder() != null) {
					try {
						Replacement[] originalReplacements = generatedItem.getItemBuilder().getReplacements();

						if (originalReplacements == null || originalReplacements.length == 0) {
							generatedItem.getItemBuilder().setReplacements(replacements);
						} else {
							generatedItem.getItemBuilder()
									.setReplacements(ArrayUtilities.join(
											new Replacement[replacements.length + originalReplacements.length],
											replacements, originalReplacements));
						}

						generatedItem.generate();

						generatedItem.getItemBuilder().setReplacements(originalReplacements);
					} catch (Exception e) {
						PandaPluginClass.getSimpleLogger()
								.sendError(String.format("Unable to process item '%s' on inventory '%s':",
										generatedItem.getItem().getId(), this.chestInventoryData.getId()));
						PandaPluginClass.getSimpleLogger().sendError(generatedItem.getItemBuilder().toString());
						e.printStackTrace();
					}
				}
			});

			this.slots.entrySet().parallelStream()
					.map(entry -> Pair.of(entry.getKey(), entry.getValue().getItemStack())).collect(Collectors.toList())
					.forEach(pair -> this.inventory.setItem(pair.getFirst(), pair.getSecond()));
		}

		List<FutureTask<ItemStack>> tasks = this.pendingPlayerHeads.stream().map(Pair::getSecond)
				.collect(Collectors.toList());
		this.pendingPlayerHeads.clear();

		if (tasks.size() > 0) {
			TasksUtils.executeOnAsync(() -> {

				tasks.forEach(task -> TasksUtils.executeOnAsync(() -> task.run()));
				tasks.forEach(task -> {
					try {
						task.get();
					} catch (InterruptedException | ExecutionException e) {
					}
				});

				TasksUtils.execute(() -> {
					Inventory topInventory = InventoryUtilities.getTopInventory(getPlayer());

					if (topInventory != null && topInventory.getHolder() == this) {
						tasks.forEach(task -> {
							try {
								ItemStack item = task.get();
								this.slots.forEach((slot, generatedItem) -> {
									if (generatedItem.getItemStack() == item) {
										this.inventory.setItem(slot, item);
									}
								});
							} catch (InterruptedException | ExecutionException e) {
							}
						});
					}
				});
			});
		}
	}

	protected void onPreUpdate() {

	}

	protected void onPostUpdate() {

	}

	public void onClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) {
			return;
		} else if (e.getClickedInventory().getHolder() != this) {
			if (e.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
				e.setCancelled(true);
			}
			return;
		}

		e.setCancelled(true);

		if (e.getClick() == ClickType.DOUBLE_CLICK) {
			return;
		}

		GeneratedItem generatedItem = slots.get(e.getRawSlot());

		if (generatedItem != null && generatedItem.getItem() != null) {
			Object object = generatedItem.getItem().onClickItem(this, generatedItem, e);
			if (object instanceof ClickResult && object == ClickResult.IGNORE) {
				e.setCancelled(false);
			}
		}
	}

	public void onDrag(InventoryDragEvent e) {
		if (e.getInventory() == null) {
			return;
		}

		if (e.getRawSlots().stream().anyMatch(slot -> slot < getSize() && slot >= 0)) {
			e.setCancelled(true);
		}
	}

	public void onClose(InventoryCloseEvent e) {

	}

	public ItemStack processPlayerHead(ItemBuilder builder, OfflinePlayer player) {
		return processPlayerHead(builder, player.getUniqueId());
	}

	public ItemStack processPlayerHead(ItemBuilder builder, UUID playerUuid) {
		if (playerHeadCacheService != null) {
			Pair<ItemStack, FutureTask<ItemStack>> itemRetrieved = playerHeadCacheService.processPlayerHead(builder,
					playerUuid);
			pendingPlayerHeads.add(itemRetrieved);
			return itemRetrieved.getFirst();
		} else {
			ItemStack item = builder.build();
			pendingPlayerHeads.add(
					new Pair<>(item, new FutureTask<>(() -> ItemStackUtilities.getPlayerHeadSync(item, playerUuid))));
			return item;
		}
	}

}