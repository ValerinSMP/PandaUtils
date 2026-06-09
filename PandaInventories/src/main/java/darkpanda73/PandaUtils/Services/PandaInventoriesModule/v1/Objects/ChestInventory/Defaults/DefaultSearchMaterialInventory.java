package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Defaults;

import java.util.function.Consumer;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged.PagedChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions.PlayerAlreadyListeningException;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services.PandaMessageListenerService;

public abstract class DefaultSearchMaterialInventory extends PagedChestInventoryObject<Material> {

	private static final String MESSAGE_MATERIALSEARCHINFO_PATH = "Messages.Material-search-info";
	private static final String MESSAGE_ALREADYINPROMPT_PATH = "Messages.Already-in-prompt";
	private static final String MESSAGE_MATERIALNOTFOUND_PATH = "Messages.Material-not-found";

	@PandaInject
	private static PandaMessageListenerService messageListenerService;

	private Consumer<Material> action;

	public DefaultSearchMaterialInventory(Player player, Consumer<Material> action) {
		super(player);

		this.action = action;
	}

	@Override
	protected ItemStack generateEntityItem(Material entity) {
		return ItemBuilder.inst().setMaterial(entity).build();
	}

	@Override
	protected void onEntityClick(InventoryClickEvent e, Material entity) {
		closeInventory();
		action.accept(entity);
	}

	@ItemExecutor("Close-button")
	private void onClickClose() {
		goToPreviousInventory();
	}

	@ItemExecutor("Search-specific-material")
	private void onClickSearchSpecificMaterial() {
		try {
			messageListenerService.getListener().startListening(getPlayer().getUniqueId(), (message) -> {
				if (!message.equalsIgnoreCase("cancel")) {
					Material materialResult = getCurrentEntityList().stream().filter(material -> {
						if (material.name().equalsIgnoreCase(message.toUpperCase())) {
							return true;
						}
						return false;
					}).findFirst().orElse(null);
					if (materialResult == null) {
						MessageTemplate
								.inst(PandaPrefixedStringField.applyPrefix(getChestInventoryData().getCustomFields()
										.get(MESSAGE_MATERIALNOTFOUND_PATH).toString()))
								.process().sendMessage(getPlayer());
					}
					action.accept(materialResult);
				} else {
					openInventory();
				}
				return true;
			});
			closeInventory();
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_MATERIALSEARCHINFO_PATH).toString()))
					.process().sendMessage(getPlayer());
		} catch (PlayerAlreadyListeningException ex) {
			MessageTemplate
					.inst(PandaPrefixedStringField.applyPrefix(
							getChestInventoryData().getCustomFields().get(MESSAGE_ALREADYINPROMPT_PATH).toString()))
					.process().sendMessage(getPlayer());
		}
	}

}
