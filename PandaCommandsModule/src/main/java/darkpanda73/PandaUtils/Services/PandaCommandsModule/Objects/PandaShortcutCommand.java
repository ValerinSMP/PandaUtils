package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public abstract class PandaShortcutCommand extends PandaCommand implements CommandExecutor {

	private String originalCommand;
	private String[] originalCommandArgs;
	private PluginCommand originalCommandInstance;

	public PandaShortcutCommand(String originalCommand) throws InstantiationException {
		super();
		this.originalCommand = originalCommand;
		this.originalCommandArgs = originalCommand.split(" ");
	}

	public PandaShortcutCommand(PandaCommandConfiguration configuration, String originalCommand)
			throws InstantiationException {
		super(configuration);
		this.originalCommand = originalCommand;
		this.originalCommandArgs = originalCommand.split(" ");
	}

	@Override
	public List<String> getAutocompleteList(Player sender, String[] args) {
		if (originalCommandInstance == null) {
			originalCommandInstance = Bukkit.getPluginCommand(originalCommandArgs[0]);
		}

		if (originalCommandInstance != null) {
			String[] newArgs = new String[args.length + originalCommandArgs.length - 1];
			for (int i = 1; i < originalCommandArgs.length; i++) {
				newArgs[i - 1] = args[i];
			}
			for (int i = 0; i < args.length; i++) {
				newArgs[i + originalCommandArgs.length - 1] = args[i];
			}
			return originalCommandInstance.tabComplete(sender, originalCommandArgs[0], newArgs);
		}
		return Collections.emptyList();
	}

	@Override
	public boolean executeCommandProcessLegacy(CommandSender sender, PandaParameters parameters) {
		Bukkit.dispatchCommand(sender,
				originalCommand + " " + parameters.getParameters().stream().collect(Collectors.joining(" ")));
		return true;
	}

}
