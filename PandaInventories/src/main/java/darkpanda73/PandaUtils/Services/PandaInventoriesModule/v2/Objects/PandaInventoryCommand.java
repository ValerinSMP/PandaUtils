package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import java.util.List;

import org.bukkit.command.CommandSender;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.PandaCommandsService;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommandConfiguration;

public class PandaInventoryCommand extends PandaCommand {

	@PandaInject
	private static PandaCommandsService commandsService;

	private PandaInventorySettings settings;
	private PandaInventoryCustomizer customizer;

	public PandaInventoryCommand(String commandName, String commandDescription, List<String> commandAliases,
			PandaInventorySettings settings, PandaInventoryCustomizer customizer) throws InstantiationException {
		super(new PandaCommandConfiguration("inventorycommand" + settings.getId(), commandName, commandName,
				commandDescription).setDefaultAliases(commandAliases.toArray(new String[commandAliases.size()])));

		this.settings = settings;
		this.customizer = customizer;
	}

	@Override
	public void initSubCommands() {
		// Empty to reduce consumption
	}

	@Override
	public boolean executeCommand(CommandSender sender, String alias, String[] args, boolean useids) {
		sender.sendMessage("Hey :D");
		// TODO: Open inventory;
		return true;
	}

}
