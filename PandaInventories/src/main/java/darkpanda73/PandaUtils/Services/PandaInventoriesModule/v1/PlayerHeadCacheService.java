package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1;

import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.FutureTask;

import org.bukkit.inventory.ItemStack;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemBuilder;
import darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackUtilities;
import darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Utils.ItemUtilities;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

@PandaService(injectable = true, loadOn = LoadStep.LOAD, priority = -997)
public class PlayerHeadCacheService {

	private static final long TTL = 2 * 60 * 1000;
	private HashMap<UUID, Pair<Long, ItemStack>> playerHeads = new HashMap<>();

	@LoadMethod
	private void load() {
		TasksUtils.executeWithTimer(
				() -> playerHeads.entrySet()
						.removeIf(entry -> (System.currentTimeMillis() - entry.getValue().getFirst()) > TTL),
				60 * 20, 60 * 20);
	}

	public Pair<ItemStack, FutureTask<ItemStack>> processPlayerHead(ItemBuilder builder, UUID playerUuid) {
		ItemStack playerHead;
		FutureTask<ItemStack> task;

		Pair<Long, ItemStack> cachedPlayerHead = playerHeads.get(playerUuid);
		if (cachedPlayerHead != null) {
			cachedPlayerHead.setFirst(System.currentTimeMillis());
			playerHead = builder.apply(cachedPlayerHead.getSecond().clone());
			task = new FutureTask<>(() -> playerHead);
		} else {
			playerHead = builder.apply(ItemUtilities.createPlayerHead());
			task = new FutureTask<>(() -> ItemStackUtilities.getPlayerHeadSync(playerHead, playerUuid));
			TasksUtils.executeOnAsync(() -> playerHeads.put(playerUuid, new Pair<>(System.currentTimeMillis(),
					ItemStackUtilities.getPlayerHeadSync(ItemUtilities.createPlayerHead(), playerUuid))));
		}

		TasksUtils.execute(() -> task.run());

		return new Pair<>(playerHead, task);
	}

}
