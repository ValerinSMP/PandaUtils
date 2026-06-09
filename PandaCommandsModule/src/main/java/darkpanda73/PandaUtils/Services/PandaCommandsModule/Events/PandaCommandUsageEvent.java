package darkpanda73.PandaUtils.Services.PandaCommandsModule.Events;

import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import lombok.Getter;
import lombok.Setter;

/**
 * Event which is generated on the usage of every command.
 * 
 * @author DarkPanda73
 */
public class PandaCommandUsageEvent extends Event implements Cancellable {

	private static final HandlerList handlerList = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlerList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

	private @Getter CommandSender commandSender;
	private @Getter PandaSubCommand command;
	private @Getter PandaParameters parameters;
	private @Getter @Setter boolean cancelled;

	public PandaCommandUsageEvent(CommandSender commandSender, PandaSubCommand command, PandaParameters parameters) {
		super();
		this.commandSender = commandSender;
		this.command = command;
		this.parameters = parameters;
	}
}
