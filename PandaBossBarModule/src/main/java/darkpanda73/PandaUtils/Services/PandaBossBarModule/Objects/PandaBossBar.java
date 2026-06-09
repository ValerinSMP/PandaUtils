package darkpanda73.PandaUtils.Services.PandaBossBarModule.Objects;

import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import lombok.Getter;

public class PandaBossBar {

	private @Getter UUID uuid;
	private @Getter boolean global;

	protected BossBar bossBar;

	public PandaBossBar(UUID uuid, boolean global) {
		this.uuid = uuid;
		this.global = global;

		this.bossBar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
	}

	public void setTitle(String title) {
		bossBar.setTitle(title);
	}

	public List<Player> getPlayers() {
		return this.bossBar.getPlayers();
	}

	public void addPlayer(Player player) {
		bossBar.addPlayer(player);
	}

	public void removePlayer(Player player) {
		bossBar.removePlayer(player);
	}

	public void setColor(BarColor color) {
		bossBar.setColor(color);
	}

	public void setPreviousColor() {
		this.setColor(BarColor.values()[bossBar.getColor() != null
				? ((BarColor.values().length + bossBar.getColor().ordinal() - 1) % BarColor.values().length)
				: 0]);
	}

	public void setNextColor() {
		this.setColor(BarColor.values()[bossBar.getColor() != null
				? ((BarColor.values().length + bossBar.getColor().ordinal() + 1) % BarColor.values().length)
				: 0]);
	}

	public void setStyle(BarStyle style) {
		bossBar.setStyle(style);
	}

	public void addFlag(BarFlag flag) {
		bossBar.addFlag(flag);
	}

	public void removeFlag(BarFlag flag) {
		bossBar.removeFlag(flag);
	}

	public void setProgress(double progress) {
		bossBar.setProgress(progress);
	}

	public void setVisible(boolean visible) {
		bossBar.setVisible(visible);
	}

	public void destroy() {
		this.bossBar.removeAll();
	}

}
