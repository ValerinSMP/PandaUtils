package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.OfflinePlayerUtilities;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.ItemUtilities;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;

public abstract class DefaultSearchPlayerInventory extends PagedChestInventoryObject<OfflinePlayer> {

	private static final String MESSAGE_PLAYERSEARCHINFO_PATH = "Messages.Player-search-info";
	private static final String MESSAGE_ALREADYINPROMPT_PATH = "Messages.Already-in-prompt";
	private static final String MESSAGE_PLAYERNOTFOUND_PATH = "Messages.Player-not-found";

	@PandaInject
	private static PandaMessageListenerService messageListenerService;

	private Consumer<OfflinePlayer> action;

	public DefaultSearchPlayerInventory(Player player, Consumer<OfflinePlayer> action) {
		super(player);

		this.action = action;
	}

	@Override
	protected List<? extends OfflinePlayer> getEntityList() {
		List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
		players.removeIf(p -> !getPlayer().canSee(p));
		return players;
	}

	@Override
	protected ItemStack generateEntityItem(OfflinePlayer entity) {
		String name = getChestInventoryData().getEntityName() != null
				&& !getChestInventoryData().getEntityName().isEmpty() ? getChestInventoryData().getEntityName() : null;

		return processPlayerHead(ItemBuilder.inst().fromItem(ItemUtilities.createPlayerHead()).setName(name)
				.setReplacements(new Replacement("{playername}", () -> entity.getName())), entity.getUniqueId());
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, OfflinePlayer entity) {
		closeInventory();
		action.accept(entity);
	}

	@ItemExecutor("Close-button")
	private void onClickClose() {
		goToPreviousInventory();
	}

	@ItemExecutor("Search-specific-player")
	private void onClickSearchSpecificPlayer() {
		try {
			messageListenerService.getListener().startListening(getPlayer().getUniqueId(), (message) -> {
				if (!message.equalsIgnoreCase("cancel")) {
					OfflinePlayer pl = OfflinePlayerUtilities.getOfflinePlayer(message);
					if (pl == null || pl.getName() == null) {
						pl = null;
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
										.get(MESSAGE_PLAYERNOTFOUND_PATH).toString()))
								.process().sendMessage(getPlayer());
					}
					action.accept(pl);
				} else {
					openInventory();
				}
				return true;
			});
			closeInventory();
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_PLAYERSEARCHINFO_PATH).toString()))
					.process().sendMessage(getPlayer());
		} catch (PlayerAlreadyListeningException ex) {
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_ALREADYINPROMPT_PATH).toString()))
					.process().sendMessage(getPlayer());
		}
	}

}
