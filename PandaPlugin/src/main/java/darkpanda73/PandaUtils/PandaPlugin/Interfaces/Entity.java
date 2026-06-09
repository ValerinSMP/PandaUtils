package darkpanda73.PandaUtils.PandaPlugin.Interfaces;

import org.bukkit.entity.Player;

public interface Entity {

	public void copy(Object fromEntity);

	public void load() throws Throwable;

	public void load(Player player) throws Throwable;

	public void save() throws Throwable;

	public void save(Player player) throws Throwable;

	public void delete() throws Throwable;

	public void delete(Player player) throws Throwable;

}