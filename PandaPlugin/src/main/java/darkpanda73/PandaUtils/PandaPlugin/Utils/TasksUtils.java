package darkpanda73.PandaUtils.PandaPlugin.Utils;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;

public class TasksUtils {

	@PandaInject
	private static PandaPluginClass plugin;

	// ---------------------------------------------------------------------//
	// Async Tasks
	// ---------------------------------------------------------------------//

	public static BukkitTask execute(Runnable runnable) {
		return execute(runnable, false);
	}

	public static BukkitTask executeOnAsync(Runnable runnable) {
		return execute(runnable, true);
	}

	public static BukkitTask execute(Runnable runnable, boolean async) {
		if (plugin.isEnabled()) {
			BukkitRunnable br = new BukkitRunnable() {
				@Override
				public void run() {
					runnable.run();
				}
			};
			if (async) {
				return br.runTaskAsynchronously(plugin);
			} else {
				return br.runTask(plugin);
			}
		}
		return null;
	}

	public static BukkitTask executeWithDelay(Runnable runnable, long startinticks) {
		return executeWithDelay(runnable, startinticks, false);
	}

	public static BukkitTask executeOnAsyncWithDelay(Runnable runnable, long startinticks) {
		return executeWithDelay(runnable, startinticks, true);
	}

	public static BukkitTask executeWithDelay(Runnable runnable, long startinticks, boolean async) {
		if (plugin.isEnabled()) {
			BukkitRunnable br = new BukkitRunnable() {
				@Override
				public void run() {
					runnable.run();
				}
			};
			if (async) {
				return br.runTaskLaterAsynchronously(plugin, startinticks);
			} else {
				return br.runTaskLater(plugin, startinticks);
			}
		}
		return null;
	}

	public static BukkitTask executeWithTimer(Runnable runnable, long startinticks, long timerinticks) {
		return executeWithTimer(runnable, startinticks, timerinticks, false);
	}

	public static BukkitTask executeOnAsyncWithTimer(Runnable runnable, long startinticks, long timerinticks) {
		return executeWithTimer(runnable, startinticks, timerinticks, true);
	}

	public static BukkitTask executeWithTimer(Runnable runnable, long startinticks, long timerinticks, boolean async) {
		if (plugin.isEnabled()) {
			BukkitRunnable br = new BukkitRunnable() {
				@Override
				public void run() {
					runnable.run();
				}
			};
			if (async) {
				return br.runTaskTimerAsynchronously(plugin, startinticks, timerinticks);
			} else {
				return br.runTaskTimer(plugin, startinticks, timerinticks);
			}
		}
		return null;
	}

}
