package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation.PandaSubCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Events.PandaCommandUsageEvent;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommandQueue.Status;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Defaults.HelpCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.FalseResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import lombok.Getter;

public abstract class PandaCommand extends PandaSubCommand implements CommandExecutor {

	public static class CommandTabCompleter implements TabCompleter {
		private PandaCommand command;

		public CommandTabCompleter(PandaCommand command) {
			this.command = command;
		}

		@Override
		public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command cmd, String label,
				String[] args) {
			return sender instanceof Player ? command.getAutocompleteList((Player) sender, args)
					: Collections.emptyList();
		}
	}

	private HelpCommand helpCommand;
	private @Getter List<PandaSubCommand> commands = new ArrayList<>();
	private @Getter List<PandaCommand> relatedCommands = new ArrayList<>();

	public PandaCommand() throws InstantiationException {
		super();
	}

	public PandaCommand(PandaCommandConfiguration configuration) throws InstantiationException {
		super(configuration);
	}

	public void initHelpCommand() throws InstantiationException {
		if (configuration.isHelpCommand()) {
			this.helpCommand = new HelpCommand();

			InjectUtils.injectOnField(this.helpCommand, "parent", this);
			InjectUtils.injectOnField(this.helpCommand, "plugin", getPlugin());
			InjectUtils.injectOnField(this.helpCommand, "eventCustomizer", getEventCustomizer());

			this.helpCommand.initFields();
		}
	}

	@SuppressWarnings("unchecked")
	public void initSubCommands() {
		try {
			initHelpCommand();
			JavaUtils.getClasses().forEach(clazz -> {
				try {
					if (PandaSubCommand.class.isAssignableFrom(clazz)
							&& clazz.isAnnotationPresent(PandaCommandAnnotation.class)) {
						try {
							PandaSubCommandAnnotation pandaSubCommand = clazz
									.getAnnotation(PandaSubCommandAnnotation.class);
							if (pandaSubCommand != null && pandaSubCommand.parentCommand() == this.getClass()) {
								PandaSubCommand subCommand;
								subCommand = ((Class<? extends PandaSubCommand>) clazz).getConstructor().newInstance();

								addCommand(subCommand);
							}
						} catch (Throwable e) {
							getPlugin().sendError(this.getClass(),
									String.format("Unable to load command '%s'", clazz.getName()));
							e.printStackTrace();
						}
					}
				} catch (Throwable e) {
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, List<PandaField<?>>> getCustomizableFileFields() {
		HashMap<String, List<PandaField<?>>> fileFields = super.getCustomizableFileFields();

		if (helpCommand != null) {
			helpCommand.getCustomizableFileFields().forEach((key, value) -> {
				List<PandaField<?>> fields = fileFields.get(key);
				if (fields != null) {
					fields.addAll(value);
				} else {
					fileFields.put(key, value);
				}
			});
		}

		getCommands().forEach(command -> {
			command.getCustomizableFileFields().forEach((key, value) -> {
				List<PandaField<?>> fields = fileFields.get(key);
				if (fields != null) {
					fields.addAll(value);
				} else {
					fileFields.put(key, value);
				}
			});
		});

		return fileFields;
	}

	public boolean addCommand(PandaSubCommand subcommand) {
		return addCommand(subcommand, commands.size());
	}

	public boolean addCommand(PandaSubCommand subcommand, int position) {
		InjectUtils.injectOnField(subcommand, "parent", this);
		InjectUtils.injectOnField(subcommand, "plugin", getPlugin());
		InjectUtils.injectOnField(subcommand, "eventCustomizer", getEventCustomizer());
		subcommand.initFields();

		if (subcommand instanceof PandaCommand) {
			((PandaCommand) subcommand).initSubCommands();
		}

		commands.add(position, subcommand);
		return true;
	}

	public boolean addRelatedCommand(PandaCommand command) {
		return this.relatedCommands.add(command);
	}

	public void sortCommands() {
		commands.sort((PandaSubCommand s1, PandaSubCommand s2) -> s1.getId().compareTo(s2.getId()));
	}

	public boolean removeCommand(PandaSubCommand subcommand) {
		return commands.remove(subcommand);
	}

	public boolean hasPendingCommand(CommandSender sender) {
		if (!super.hasPendingCommand(sender)) {
			for (PandaSubCommand command : this.commands) {
				if (command.hasPendingCommand(sender)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public final boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] args) {
		return this.executeCommand(sender, label, args, false);
	}

	public boolean executeCommand(CommandSender sender, String alias, String[] args, boolean useids) {
		if (!getCurrentPermission().isEmpty() && !sender.hasPermission(getCurrentPermission())) {
			return false;
		}

		if (this.getParent() == null && hasPendingCommand(sender)) {
			MessageTemplate.inst(ERROR_COMMANDS_PENDINGCOMMAND.applyPrefix()).process().sendMessage(sender);
			return true;
		}

		if (args.length != 0) {
			String scmd = args[0].toLowerCase();
			if (this.helpCommand != null && scmd.equals(this.helpCommand.getCurrentName().toLowerCase())) {
				return this.helpCommand.executeCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length));
			}
			for (PandaSubCommand command : this.commands) {
				if (((!useids && (scmd.equals(command.getCurrentName().toLowerCase())
						|| command.getCurrentAliases().contains(scmd))) || (useids && scmd.equals(command.getId())))) {
					if (command instanceof PandaCommand
							? ((PandaCommand) command).executeCommand(sender, args[0],
									Arrays.copyOfRange(args, 1, args.length), useids)
							: command.executeCommand(sender, args[0], Arrays.copyOfRange(args, 1, args.length))) {
						return true;
					} else {
						return this.helpCommand != null
								? this.helpCommand.executeCommand(sender, args[0],
										Arrays.copyOfRange(args, 1, args.length))
								: false;
					}
				}
			}
		}

		if (getCurrentCooldown() > 0) {
			Status status = getQueue().getCooldownStatus(sender);

			if (status == Status.COOLDOWN) {
				MessageTemplate.inst(ERROR_COMMANDS_COOLDOWNACTIVE.applyPrefix())
						.setReplacements(new Replacement("%seconds%",
								() -> String.valueOf((int) (getQueue().getCooldownRemaining(sender) / 1000))))
						.process().sendMessage(sender);
				return true;
			}
		}

		PandaParameters parameters = PandaParameters.from(alias, getRegisteredKeyOnlyParameters(),
				getRegisteredKeyValueParameters(), args);

		PandaCommandUsageEvent event = getEventCustomizer() != null
				? getEventCustomizer().generateEvent(PandaCommandUsageEvent.class,
						new Class<?>[] { CommandSender.class, PandaSubCommand.class, PandaParameters.class },
						new Object[] { sender, this, parameters },
						() -> new PandaCommandUsageEvent(sender, this, parameters))
				: new PandaCommandUsageEvent(sender, this, parameters);
		Bukkit.getPluginManager().callEvent(event);

		if (event.isCancelled()) {
			return true;
		}

		CommandResponse response = executeCommandProcess(sender, parameters);

		if (response instanceof FalseResponse) {
			return this.helpCommand != null
					? this.helpCommand.executeCommand(sender, this.helpCommand.getCurrentName(), new String[0])
					: false;
		} else {
			getQueue().addCooldown(sender, response);
			return true;
		}
	}

	/*
	 * Autocomplete methods
	 */

	@Override
	public List<String> getAutocompleteList(Player sender, String[] args) {
		if (!getCurrentPermission().isEmpty() && !sender.hasPermission(getCurrentPermission())) {
			return Collections.emptyList();
		}

		if (args.length > 1) {
			String scmd = args[0].toLowerCase();

			if (helpCommand != null && scmd.equals(helpCommand.getCurrentName().toLowerCase())) {
				return helpCommand.getAutocompleteList(sender, Arrays.copyOfRange(args, 1, args.length));
			}

			for (PandaSubCommand command : commands) {
				if (scmd.equals(command.getCurrentName().toLowerCase()) || command.getCurrentAliases().contains(scmd)) {
					return command.getAutocompleteList(sender, Arrays.copyOfRange(args, 1, args.length));
				}
			}
		}

		return super.getAutocompleteList(sender, args);
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		if (argIndex == 0) {
			List<String> list = new ArrayList<>();
			if (helpCommand != null) {
				list.add(helpCommand.getCurrentName().toLowerCase());
			}
			for (PandaSubCommand command : commands) {
				if (command.precondition() && (command.getCurrentPermission().isEmpty()
						|| sender.hasPermission(command.getCurrentPermission()))) {
					list.add(command.getCurrentName());
				}
			}
			return list;
		}
		return super.generateAutocompleteList(sender, argIndex);
	}

	@Override
	public void clearCachedAutocompleteLists() {
		super.clearCachedAutocompleteLists();
		this.commands.forEach(PandaSubCommand::clearCachedAutocompleteLists);
	}

}
