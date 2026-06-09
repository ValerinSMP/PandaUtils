package darkpanda73.PandaUtils.Services.PandaPlayerDataModule;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Exception.SizeLimitExceededException;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Listeners.PandaPlayerDataEvents;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Objects.IndexedList;
import darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Objects.PlayerData;
import lombok.Getter;

@PandaService(loadOn = LoadStep.ENABLE, priority = 0)
public abstract class PandaPlayerDataService<T extends PlayerData> {

	public static final String METADATA_KEY = "PlayerDataIndex";

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaPlayerDataEvents events;

	private @Getter IndexedList<T> playerDatas = new IndexedList<>(Short.MAX_VALUE);

	@PostInjectMethod
	private void postInject() {
		if (events == null) {
			plugin.sendError(this.getClass(),
					"Warning! The PandaPlayerDataEvents class is not loaded and it'll not be listening. We suggest to load it as it's used to optimized the load of the data!");
		}
	}

	@LoadMethod
	protected void load() {
		Bukkit.getOnlinePlayers().forEach(player -> {
			try {
				int index = loadPlayerData(player.getUniqueId());

				player.setMetadata(METADATA_KEY, new FixedMetadataValue(plugin, index));
			} catch (SizeLimitExceededException e) {
				e.printStackTrace();
			}
		});

		TasksUtils.executeWithTimer(() -> {
			playerDatas.removeIf((playerData) -> {
				return Bukkit.getPlayer(playerData.getUuid()) == null;
			});
		}, 120, 120);
	}

	@UnloadMethod
	protected void unload() {
		playerDatas.clear();
	}

	public int getSize() {
		return this.playerDatas.getSize();
	}

	public int getOccupied() {
		return this.playerDatas.getOccupied();
	}

	public int loadPlayerData(UUID uuid) throws SizeLimitExceededException {
		T data = generatePlayerData(uuid);
		int index = this.playerDatas.add(data);

		if (index == -1) {
			throw new SizeLimitExceededException();
		}

		return index;
	}

	public T getPlayerData(int index) {
		return playerDatas.get(index);
	}

	public T getPlayerData(UUID uuid) {
		return this.getPlayerData(uuid, false);
	}

	public T getPlayerData(UUID uuid, boolean generateIfNull) {
		Player pl = Bukkit.getPlayer(uuid);

		if (pl != null) {
			T playerData = getPlayerData(pl);
			if (playerData != null) {
				return playerData;
			}
		}

		int index = playerDatas.findIndex(uuid);

		if (index != -1) {
			return getPlayerData(index);
		}

		return generateIfNull ? generatePlayerData(uuid) : null;
	}

	public T getPlayerData(OfflinePlayer player) {
		return getPlayerData(player.getUniqueId());
	}

	public T getPlayerData(OfflinePlayer player, boolean generateIfNull) {
		return getPlayerData(player.getUniqueId(), generateIfNull);
	}

	public T getPlayerData(Player player) {
		MetadataValue index = player.getMetadata(METADATA_KEY).stream()
				.filter(metadata -> metadata.getOwningPlugin() == plugin).findFirst().orElse(null);
		if (index != null) {
			return getPlayerData(index.asInt());
		}
		return null;
	}

	public void unloadPlayerData(Player player) {
		MetadataValue index = player.getMetadata(METADATA_KEY).stream()
				.filter(metadata -> metadata.getOwningPlugin() == plugin).findFirst().orElse(null);
		if (index != null) {
			this.playerDatas.remove(index.asInt());
		} else {
			int intIndex = -1;
			while ((intIndex = this.playerDatas.findIndex(player.getUniqueId())) != -1) {
				this.playerDatas.remove(intIndex);
			}
		}
	}

	protected abstract T generatePlayerData(UUID uuid);
}
