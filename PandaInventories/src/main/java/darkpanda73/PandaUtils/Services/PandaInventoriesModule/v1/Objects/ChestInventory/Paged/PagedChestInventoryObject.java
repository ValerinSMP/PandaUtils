package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Paged;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemGenerator;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import darkpanda73.PandaUtils.Utilities.Java.Methods.MethodFinder;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import lombok.Getter;
import lombok.Setter;

public abstract class PagedChestInventoryObject<E> extends ChestInventoryObject {

	private static final Item ENTITY_ITEM = new Item("Entity", Collections.emptyMap())
			.setOnClickItemMethod(MethodFinder.forClass(PagedChestInventoryObject.class).setName("onEntityClick")
					.setParams(Item.class, EntityItem.class, InventoryClickEvent.class).findSafely().getMethod());

	private @Getter @Setter boolean updateListOnUpdateInventory = false;

	private List<? extends E> entityList;
	private @Getter @Setter int currentPage = 1;
	private @Getter int currentMaxPage = 1;

	public PagedChestInventoryObject(Player player) {
		super(player);
		setTitleReplacements();
	}

	@Override
	public void setTitleReplacements(Replacement... replacements) {
		Replacement[] withPages = new Replacement[replacements.length + 2];
		System.arraycopy(replacements, 0, withPages, 0, replacements.length);
		withPages[replacements.length] = new Replacement("{current_page}", () -> Integer.toString(currentPage));
		withPages[replacements.length + 1] = new Replacement("{max_page}", () -> Integer.toString(currentMaxPage));
		super.setTitleReplacements(withPages);
	}

	@Override
	public PagedChestInventoryData getChestInventoryData() {
		return (PagedChestInventoryData) super.getChestInventoryData();
	}

	@Override
	protected void onPreUpdate() {
		super.onPreUpdate();

		if (entityList == null || updateListOnUpdateInventory) {
			updateEntityList();
		}

		currentMaxPage = getMaxPage();

		if (currentPage < 1) {
			currentPage = 1;
		} else if (currentPage > currentMaxPage) {
			currentPage = currentMaxPage > 0 ? currentMaxPage : 1;
		}
	}

	@Override
	protected void onPostUpdate() {
		PagedChestInventoryData data = (PagedChestInventoryData) this.getChestInventoryData();

		int[] entitySlots = data.getEntitySlots();

		int offsetPerPage = this.getOffsetPerPage();

		if (entitySlots.length > 0 && this.entityList.size() != 0) {
			int slot = 0;
			int startIndex = (currentPage - 1) * offsetPerPage;
			int maxIndex = startIndex + entitySlots.length;
			for (int i = startIndex; i < maxIndex; i++) {
				if (i >= this.entityList.size()) {
					break;
				}

				E entity = this.entityList.get(i);

				this.setSlot(entitySlots[slot++],
						new EntityItem<E>(ENTITY_ITEM, generateEntityItem(entity, i), getPlayer(), entity, i));

				if (slot >= entitySlots.length) {
					break;
				}
			}
		}
	}

	@ItemGenerator("Previous-page-button")
	private ItemStack generateLeftPageButton(Item item) {
		return currentPage > 1 ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("Previous-page-button")
	public void onClickLeftPageButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		currentPage--;
		updateInventory();
	}

	@ItemGenerator("Next-page-button")
	private ItemStack generateRightPageButton(Item item) {
		return currentPage < currentMaxPage ? item.getItems().get(Item.DISPLAYITEM_KEY) : null;
	}

	@ItemExecutor("Next-page-button")
	public void onClickRightPageButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		currentPage++;
		updateInventory();
	}

	@SuppressWarnings("unused")
	private void onEntityClick(Item item, EntityItem<E> generatedItem, InventoryClickEvent e) {
		onEntityClick(e, generatedItem.getEntity(), generatedItem.getIndex());
	}

	protected int getMaxPage() {
		PagedChestInventoryData data = (PagedChestInventoryData) this.getChestInventoryData();

		int offsetPerPage = getOffsetPerPage();

		return data.getEntitySlots().length > 0 && getCurrentEntityList().size() > data.getEntitySlots().length
				? ((((getCurrentEntityList().size() - data.getEntitySlots().length) + (offsetPerPage - 1))
						/ offsetPerPage) + 1)
				: 1;
	}

	protected List<? extends E> getCurrentEntityList() {
		return this.entityList;
	}

	protected int getOffsetPerPage() {
		PagedChestInventoryData data = (PagedChestInventoryData) this.getChestInventoryData();

		int[] entitySlots = data.getEntitySlots();

		return entitySlots.length;
	}

	public void updateEntityList() {
		this.entityList = getEntityList();
	}

	protected abstract List<? extends E> getEntityList();

	protected ItemStack generateEntityItem(E entity) {
		return null;
	}

	protected ItemStack generateEntityItem(E entity, int index) {
		return generateEntityItem(entity);
	}

	protected void onEntityClick(InventoryClickEvent e, E entity, int index) {
		// Keep old behaviour
		onEntityClick(e, entity);
	}

	protected void onEntityClick(InventoryClickEvent e, E entity) {
	}

}
