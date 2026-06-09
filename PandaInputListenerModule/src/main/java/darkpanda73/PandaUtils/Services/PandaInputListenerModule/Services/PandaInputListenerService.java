package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Services;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitTask;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Exceptions.PlayerAlreadyListenedException;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Exceptions.PlayerNotListenedException;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.AnvilMessageInputListener;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.AnvilMessageInputListener.AnvilCallback;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.BlockInputListener;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.InputListener;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.InputListener.Callback;
import darkpanda73.PandaUtils.Services.PandaInputListenerModule.Objects.MessageInputListener;

@PandaService(loadOn = LoadStep.ENABLE)
public class PandaInputListenerService implements Listener {

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_INPUTLISTENER_MESSAGELISTENERPRIORITY = new PandaStringField(
			"Settings.Input-listener.Message-listener-priority", EventPriority.LOWEST.name());

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_INPUTLISTENER_BLOCKLISTENERPRIORITY = new PandaStringField(
			"Settings.Input-listener.Block-listener-priority", EventPriority.LOWEST.name());

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_INPUTLISTENER_INVENTORYLISTENERPRIORITY = new PandaStringField(
			"Settings.Input-listener.Inventory-listener-priority", EventPriority.LOWEST.name());

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private Map<UUID, InputListener<?>> listeners = new HashMap<>();

	private BukkitTask anvilTask;

	@PostInjectMethod
	private void postInject() {
		PandaYamlFile file = filesService.getYaml("config");
		if (file != null) {
			file.registerFieldsFromClass(PandaInputListenerService.class);
			file.registerFieldsFromClass(InputListener.class);
		}
	}

	@LoadMethod
	private void load() {
		InjectUtils.inject(AnvilMessageInputListener.class, this);

		anvilTask = TasksUtils.executeWithTimer(() -> {
			listeners.values().forEach(listener -> {
				if (listener instanceof AnvilMessageInputListener) {
					((AnvilMessageInputListener) listener).getAnvilHolder().getInventory().setItem(2,
							ItemBuilder.inst().setMaterial("NAME_TAG").setName("&7&o---").build());
				}
			});
		}, 0, 0);

		Bukkit.getPluginManager().registerEvent(PlayerQuitEvent.class, this, EventPriority.MONITOR, (l, e) -> {
			if (e instanceof PlayerQuitEvent) {
				this.listeners.remove(((PlayerQuitEvent) e).getPlayer().getUniqueId());
			}
		}, plugin);

		Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_MESSAGELISTENERPRIORITY.getContent().toUpperCase()), (l, e) -> {
					if (e instanceof AsyncPlayerChatEvent) {
						onMessageEvent((AsyncPlayerChatEvent) e);
					}
				}, plugin, false);

		Bukkit.getPluginManager().registerEvent(PlayerInteractEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_BLOCKLISTENERPRIORITY.getContent().toUpperCase()), (l, e) -> {
					if (e instanceof PlayerInteractEvent) {
						onInteractEvent((PlayerInteractEvent) e);
					}
				}, plugin, false);

		Bukkit.getPluginManager().registerEvent(InventoryInteractEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_INVENTORYLISTENERPRIORITY.getContent().toUpperCase()),
				(l, e) -> {
					if (e instanceof InventoryInteractEvent) {
						onInventoryInteractEvent((InventoryInteractEvent) e);
					}
				}, plugin, false);

		Bukkit.getPluginManager().registerEvent(InventoryCloseEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_INVENTORYLISTENERPRIORITY.getContent().toUpperCase()),
				(l, e) -> {
					if (e instanceof InventoryCloseEvent) {
						onInventoryCloseEvent((InventoryCloseEvent) e);
					}
				}, plugin, false);

		Bukkit.getPluginManager().registerEvent(InventoryClickEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_INVENTORYLISTENERPRIORITY.getContent().toUpperCase()),
				(l, e) -> {
					if (e instanceof InventoryClickEvent) {
						onInventoryClickEvent((InventoryClickEvent) e);
					}
				}, plugin, false);

		Bukkit.getPluginManager().registerEvent(InventoryDragEvent.class, this,
				toEventPriority(SETTINGS_INPUTLISTENER_INVENTORYLISTENERPRIORITY.getContent().toUpperCase()),
				(l, e) -> {
					if (e instanceof InventoryDragEvent) {
						onInventoryDragEvent((InventoryDragEvent) e);
					}
				}, plugin, false);
	}

	@UnloadMethod
	private void unload() {
		this.listeners.values().forEach(listener -> listener.cancel());
		this.listeners.clear();

		HandlerList.unregisterAll(this);

		if (this.anvilTask != null) {
			this.anvilTask.cancel();
			this.anvilTask = null;
		}
	}

	@ReloadMethod
	private void reload() {
		unload();
		load();
	}

	public void registerMessageListener(UUID playerUuid, Callback<String> callback)
			throws PlayerAlreadyListenedException {
		if (this.listeners.containsKey(playerUuid)) {
			throw new PlayerAlreadyListenedException();
		}

		this.registerListener(playerUuid, new MessageInputListener(playerUuid, callback));
	}

	public void registerBlockListener(UUID playerUuid, Callback<Block> callback) throws PlayerAlreadyListenedException {
		if (this.listeners.containsKey(playerUuid)) {
			throw new PlayerAlreadyListenedException();
		}

		this.registerListener(playerUuid, new BlockInputListener(playerUuid, callback));
	}

	public void registerAnvilMessageListener(UUID playerUuid, AnvilCallback callback, String anvilTitle)
			throws PlayerAlreadyListenedException {
		if (this.listeners.containsKey(playerUuid)) {
			throw new PlayerAlreadyListenedException();
		}

		this.registerListener(playerUuid, new AnvilMessageInputListener(playerUuid, callback, anvilTitle));
	}

	public void unregisterListener(UUID playerUuid) throws PlayerNotListenedException {
		if (!this.listeners.containsKey(playerUuid)) {
			throw new PlayerNotListenedException();
		}

		this.listeners.remove(playerUuid);
	}

	private void registerListener(UUID playerUuid, InputListener<?> listener) {
		this.listeners.put(playerUuid, listener);

		listener.onProcessStartEvent();
	}

	private EventPriority toEventPriority(String eventPriority) {
		try {
			return EventPriority.valueOf(eventPriority);
		} catch (IllegalArgumentException e) {
			return EventPriority.LOWEST;
		}
	}

	private void onMessageEvent(AsyncPlayerChatEvent e) {
		if (!e.isCancelled()) {
			InputListener<?> listener = listeners.get(e.getPlayer().getUniqueId());

			if (listener != null && !listener.isCancelled()) {
				listener.onMessageEvent(e);
			}
		}
	}

	private void onInteractEvent(PlayerInteractEvent e) {
		InputListener<?> listener = listeners.get(e.getPlayer().getUniqueId());

		if (listener != null && !listener.isCancelled()) {
			listener.onInteractEvent(e);
		}
	}

	public void onInventoryInteractEvent(InventoryInteractEvent e) {
		if (!e.isCancelled()) {
			InputListener<?> listener = listeners.get(e.getWhoClicked().getUniqueId());

			if (listener != null && !listener.isCancelled()) {
				listener.onInventoryInteractEvent(e);
			}
		}
	}

	public void onInventoryCloseEvent(InventoryCloseEvent e) {
		InputListener<?> listener = listeners.get(e.getPlayer().getUniqueId());

		if (listener != null && !listener.isCancelled()) {
			listener.onInventoryCloseEvent(e);
		}
	}

	public void onInventoryClickEvent(InventoryClickEvent e) {
		if (!e.isCancelled()) {
			InputListener<?> listener = listeners.get(e.getWhoClicked().getUniqueId());

			if (listener != null && !listener.isCancelled()) {
				listener.onInventoryClickEvent(e);
			}
		}
	}

	public void onInventoryDragEvent(InventoryDragEvent e) {
		if (!e.isCancelled()) {
			InputListener<?> listener = listeners.get(e.getWhoClicked().getUniqueId());

			if (listener != null && !listener.isCancelled()) {
				listener.onInventoryDragEvent(e);
			}
		}
	}

}
