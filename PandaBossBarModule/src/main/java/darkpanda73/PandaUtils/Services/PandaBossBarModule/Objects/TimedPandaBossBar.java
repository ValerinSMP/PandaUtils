package darkpanda73.PandaUtils.Services.PandaBossBarModule.Objects;

import java.util.UUID;

import org.bukkit.scheduler.BukkitTask;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Services.PandaBossBarModule.PandaBossBarService;
import lombok.Getter;
import lombok.Setter;

public class TimedPandaBossBar extends PandaBossBar {

	@PandaInject
	private static PandaBossBarService pandaBossBarService;

	private @Getter @Setter long durationInTicks;
	private @Getter long currentTick = 0;

	private @Setter @Getter boolean removeOnFinish;

	private @Setter Runnable customBeforeOnTick;
	private @Setter Runnable customAfterOnTick;

	private BukkitTask bukkitTask;

	public TimedPandaBossBar(UUID uuid, boolean global, long durationInTicks) {
		this(uuid, global, durationInTicks, true);
	}

	public TimedPandaBossBar(UUID uuid, boolean global, long durationInTicks, boolean removeOnFinish) {
		super(uuid, global);

		this.durationInTicks = durationInTicks;
		this.removeOnFinish = removeOnFinish;

		this.setVisible(false);
	}

	public void reset() {
		start();
	}

	public void start() {
		this.setVisible(true);
		this.currentTick = 0;

		if (this.bukkitTask == null) {
			this.bukkitTask = TasksUtils.executeWithTimer(() -> {
				onTick();
			}, 0L, 1L);
		}
	}

	public void stop() {
		if (this.bukkitTask != null) {
			this.bukkitTask.cancel();
			this.bukkitTask = null;
			this.setVisible(false);
		}
	}

	public boolean isRunning() {
		return this.bukkitTask != null;
	}

	@Override
	public void destroy() {
		this.stop();

		super.destroy();
	}

	protected void onTick() {
		if (this.customBeforeOnTick != null) {
			this.customBeforeOnTick.run();
		}

		if (this.currentTick++ >= durationInTicks) {
			if (this.removeOnFinish) {
				pandaBossBarService.removeBossBar(getUuid());
			} else {
				this.stop();
			}
		} else {
			this.bossBar.setProgress((double) (durationInTicks - currentTick) / (double) durationInTicks);
		}

		if (this.customAfterOnTick != null) {
			this.customAfterOnTick.run();
		}
	}

}
