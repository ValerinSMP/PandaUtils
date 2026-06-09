package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;

public abstract class DefaultSearchWorldInventory extends PagedChestInventoryObject<World> {

	private static final String MESSAGE_WORLDSEARCHINFO_PATH = "Messages.World-search-info";
	private static final String MESSAGE_ALREADYINPROMPT_PATH = "Messages.Already-in-prompt";
	private static final String MESSAGE_WORLDNOTFOUND_PATH = "Messages.World-not-found";

	@PandaInject
	private static PandaMessageListenerService messageListenerService;

	private Consumer<World> action;

	public DefaultSearchWorldInventory(Player player, Consumer<World> action) {
		super(player);

		this.action = action;
	}

	@Override
	protected List<World> getEntityList() {
		return Bukkit.getWorlds();
	}

	@Override
	protected ItemStack generateEntityItem(World entity) {
		return ItemBuilder.inst().fromMap(getChestInventoryData().getCustomFields(), "Entity")
				.setReplacements(new Replacement("{worldname}", () -> entity.getName())).build();
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, World entity) {
		closeInventory();
		action.accept(entity);
	}

	@ItemExecutor("Close-button")
	private void onClickClose() {
		goToPreviousInventory();
	}

	@ItemExecutor("Search-specific-world")
	private void onClickSearchSpecificPlayer() {
		try {
			messageListenerService.getListener().startListening(getPlayer().getUniqueId(), (message) -> {
				if (!message.equalsIgnoreCase("cancel")) {
					Optional<? extends World> world = getCurrentEntityList().stream()
							.filter(w -> w.getName().equalsIgnoreCase(message)).findFirst();
					if (!world.isPresent()) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
										.get(MESSAGE_WORLDNOTFOUND_PATH).toString()))
								.process().sendMessage(getPlayer());
					}
					action.accept(world.orElse(null));
				} else {
					openInventory();
				}
				return true;
			});
			closeInventory();
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_WORLDSEARCHINFO_PATH).toString()))
					.process().sendMessage(getPlayer());
		} catch (PlayerAlreadyListeningException ex) {
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_ALREADYINPROMPT_PATH).toString()))
					.process().sendMessage(getPlayer());
		}
	}

}
