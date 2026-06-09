package darkpanda73.PandaUtils.Services.PandaBossBarModule;

import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.function.Supplier;

import org.bukkit.Bukkit;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.Objects.PandaBossBar;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.Objects.TimedPandaBossBar;

@PandaService(priority = 1)
public class PandaBossBarService {

	private HashMap<UUID, PandaBossBar> bossBars = new HashMap<>();

	@LoadMethod
	private void load() {
		InjectUtils.inject(TimedPandaBossBar.class, this);
	}

	@UnloadMethod
	private void unload() {
		bossBars.values().forEach(PandaBossBar::destroy);
		bossBars.clear();
	}

	public Collection<PandaBossBar> getBossBars() {
		return bossBars.values();
	}

	public PandaBossBar createBossBar() {
		return createBossBar(false);
	}

	public PandaBossBar createGlobalBossBar() {
		PandaBossBar pandaBossBar = createBossBar(true);

		Bukkit.getOnlinePlayers().forEach(pandaBossBar::addPlayer);

		return pandaBossBar;
	}

	public TimedPandaBossBar createTimedBossBar(long durationInTicks) {
		return createTimedBossBar(false, durationInTicks);
	}

	public TimedPandaBossBar createTimedGlobalBossBar(long durationInTicks) {
		TimedPandaBossBar pandaBossBar = createTimedBossBar(true, durationInTicks);

		Bukkit.getOnlinePlayers().forEach(pandaBossBar::addPlayer);

		return pandaBossBar;
	}

	private synchronized PandaBossBar createBossBar(boolean global) {
		return executeSynchronizedWithReturn(() -> {
			UUID uuid = null;
			do {
				uuid = UUID.randomUUID();
			} while (bossBars.containsKey(uuid));

			PandaBossBar pandaBossBar = new PandaBossBar(uuid, global);

			bossBars.put(uuid, pandaBossBar);

			return pandaBossBar;
		});
	}

	private synchronized TimedPandaBossBar createTimedBossBar(boolean global, long durationInTicks) {
		return executeSynchronizedWithReturn(() -> {
			UUID uuid = null;
			do {
				uuid = UUID.randomUUID();
			} while (bossBars.containsKey(uuid));

			TimedPandaBossBar pandaBossBar = new TimedPandaBossBar(uuid, global, durationInTicks);

			bossBars.put(uuid, pandaBossBar);

			return pandaBossBar;
		});
	}

	public boolean removeBossBar(UUID uuid) {
		PandaBossBar pandaBossBar = this.bossBars.remove(uuid);

		if (pandaBossBar != null) {
			pandaBossBar.destroy();
			return true;
		}

		return false;
	}

	public <T> T executeSynchronizedWithReturn(Supplier<T> func) {
		synchronized (this) {
			return func.get();
		}
	}

	public void executeSynchronized(Runnable func) {
		executeSynchronizedWithReturn(() -> {
			func.run();
			return null;
		});
	}

}
