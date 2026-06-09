package darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.Objects;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONArray;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONObject;
import darkpanda73.PandaUtils.PandaJSON.Utils.PandaJSONUtilities;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCachedPlayersModule.PandaCachedPlayersService;
import darkpanda73.PandaUtils.Utilities.Http.HttpClient;
import darkpanda73.PandaUtils.Utilities.Http.HttpClient.Method;
import darkpanda73.PandaUtils.Utilities.Http.HttpClient.Response;
import darkpanda73.PandaUtils.Utilities.Java.Objects.ObjectUtilities;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public class PandaCachedPlayer {

	@PandaInject
	private static PandaCachedPlayersService service;

	private @Getter UUID uuid;

	private @Getter String name;

	private FutureTask<String> skin;

	private @Getter @Setter(AccessLevel.NONE) long lastRequest = System.currentTimeMillis();

	public PandaCachedPlayer(UUID uuid, String name) {
		this.uuid = uuid;
		this.name = name;
		this.skin = new FutureTask<String>(() -> {
			try {
				PandaPluginClass.getSimpleLogger()
						.sendDebug(String.format("Downloading skin from player '%s'...", name));

				HttpClient client = new HttpClient(
						"https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString(), Method.GET)
						.setTimeout(5000);

				Response response = client.send();

				if (response != null && response.getBody() != null) {
					PandaJSONObject json = PandaJSONUtilities.parseJson(new String(response.getBody())).asObject();

					if (json != null && json.has("properties")) {
						PandaJSONArray propertiesJson = json.getArray("properties");

						if (propertiesJson.values().size() > 0) {
							for (int i = 0; i < propertiesJson.values().size(); i++) {
								PandaJSONObject texturesJson = propertiesJson.getObject(i);

								if (texturesJson.has("name") && texturesJson.has("value")
										&& "textures".equalsIgnoreCase(ObjectUtilities
												.firstNonNull(texturesJson.get("name"), "").toString())) {
									PandaPluginClass.getSimpleLogger().sendDebug(
											String.format("Downloaded skin from player '%s' successfully.", name));

									return String.valueOf(texturesJson.get("value"));
								}
							}
						}
					}
				}
			} catch (Throwable e) {
				PandaPluginClass.getSimpleLogger().sendError(
						String.format("There was an error trying to download the skin from player '%s':", name));
				e.printStackTrace();
			}

			PandaPluginClass.getSimpleLogger()
					.sendDebug(String.format("Unable to download skin from player '%s'.", name));

			return null;
		});
		service.getAsyncQueue().add(this.skin);
	}

	public PandaCachedPlayer(UUID uuid, String name, String skin) {
		this.uuid = uuid;
		this.name = name;
		this.skin = new FutureTask<String>(() -> skin);
		service.getAsyncQueue().add(this.skin);
	}

	public String getSkin() throws InterruptedException, ExecutionException, TimeoutException {
		return this.skin.get(30000, TimeUnit.MILLISECONDS);
	}

	public FutureTask<String> getSkinSupplier() {
		return this.skin;
	}

	public void updateLastRequest() {
		this.lastRequest = System.currentTimeMillis();
	}

	public OfflinePlayer toBukkit() {
		return Bukkit.getOfflinePlayer(uuid);
	}

}
