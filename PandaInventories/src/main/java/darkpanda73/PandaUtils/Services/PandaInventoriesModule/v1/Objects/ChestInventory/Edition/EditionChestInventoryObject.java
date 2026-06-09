package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Edition;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Annotations.ItemExecutor;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.ChestInventoryObject;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.GeneratedItem;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory.Item;
import lombok.Getter;

public abstract class EditionChestInventoryObject<T extends EditionChestInventoryObject.EditableEntity<T>>
		extends ChestInventoryObject {

	public static interface EditableEntity<T> {

		public void copy(T fromEntity);

		public void save() throws Throwable;

		public void save(Player player) throws Throwable;

		public void delete() throws Throwable;

		public void delete(Player player) throws Throwable;

	}

	private T originalEntity;
	private T copyEntity;
	private @Getter T editableEntity;

	private @Getter boolean newEntity = true;

	public EditionChestInventoryObject(Player player) {
		this(player, null);
	}

	public EditionChestInventoryObject(Player player, T entity) {
		super(player);

		this.newEntity = entity == null;
		this.originalEntity = entity != null ? entity : newEntityInstance();
		this.copyEntity = newEntityInstance();
		this.copyEntity.copy(this.originalEntity);
		this.editableEntity = newEntityInstance();
		this.editableEntity.copy(this.originalEntity);
	}

	@ItemExecutor("Save-button")
	private void onClickSaveButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		try {
			onSave();
		} catch (Throwable e1) {
			e1.printStackTrace();
		}
	}

	@ItemExecutor("Cancel-button")
	private void onClickCancelButton(Item item, GeneratedItem generatedItem, InventoryClickEvent e) {
		goToPreviousInventory();
	}

	public void onSave() throws Throwable {
		try {
			this.originalEntity.copy(this.editableEntity);
			this.originalEntity.save(getPlayer());
			goToPreviousInventory();
			onSaveSuccess();
		} catch (Throwable e) {
			this.originalEntity.copy(this.copyEntity);
			onSaveError(e);
		}
	}

	/*
	 * Abstracts
	 */

	public abstract T newEntityInstance();

	public abstract void onSaveSuccess();

	public abstract void onSaveError(Throwable throwable) throws Throwable;

}
