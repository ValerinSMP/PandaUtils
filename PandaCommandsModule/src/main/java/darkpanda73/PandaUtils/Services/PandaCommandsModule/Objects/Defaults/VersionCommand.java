package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Defaults;

import org.bukkit.command.CommandSender;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;

@PandaCommandAnnotation(
		id = "version",
		pathName = "Version",
		defaultName = "version",
		defaultDescription = "Returns the current version of the plugin",
		defaultAliases = { "v" })
@PandaCommandAnnotation.Customizable(permission = true, usage = true, aliases = true)
public class VersionCommand extends PandaSubCommand {

	public VersionCommand() throws InstantiationException {
		super();
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		MessageTemplate
				.inst(String.format("&6%s: &e%s", getPlugin().getName(), getPlugin().getDescription().getVersion()))
				.process().sendMessage(sender);
		return CommandResponse.trueResponse();
	}

}
