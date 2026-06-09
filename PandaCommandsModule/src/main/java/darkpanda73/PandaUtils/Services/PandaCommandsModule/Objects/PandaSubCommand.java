package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Events.PandaCommandUsageEvent;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommandQueue.Status;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.FalseResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaEventCustomizer.PandaEventCustomizerService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaLongField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;
import lombok.NonNull;

@Getter
public abstract class PandaSubCommand {

	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField ERROR_COMMANDS_PERMISSIONDENIED = new PandaPrefixedStringField(
			"Error.Commands.Permission-denied", "&cYou don't have permissions to use this command!");
	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField ERROR_COMMANDS_PENDINGCOMMAND = new PandaPrefixedStringField(
			"Error.Commands.Pending-command", "&cYou have a command still executing!");
	@RegisteredPandaField("lang")
	public static final PandaPrefixedStringField ERROR_COMMANDS_COOLDOWNACTIVE = new PandaPrefixedStringField(
			"Error.Commands.Cooldown-active",
			"&cYou have to wait for the next &e%seconds% seconds &cto execute this command again!");

	@PandaInject
	private @Getter(lombok.AccessLevel.PROTECTED) PandaPluginClass plugin;

	@PandaInject
	private @Getter(lombok.AccessLevel.PROTECTED) PandaEventCustomizerService eventCustomizer;

	@PandaInject
	private PandaCommand parent;

	private PandaCommandQueue queue = new PandaCommandQueue(this);

	protected @Getter(lombok.AccessLevel.NONE) PandaCommandConfiguration configuration;

	protected PandaStringField nameField;
	protected PandaStringField descriptionField;
	protected PandaStringField permissionField;
	protected PandaStringField usageField;
	protected PandaStringListField aliasesField;
	protected PandaLongField cooldownField;

	public PandaSubCommand() throws InstantiationException {
		PandaCommandAnnotation pandaCommand = this.getClass().getAnnotation(PandaCommandAnnotation.class);
		PandaCommandAnnotation.Customizable pandaCommandCustomizable = this.getClass()
				.getAnnotation(PandaCommandAnnotation.Customizable.class);

		if (pandaCommand == null) {
			throw new InstantiationException("Command does not have the annotation PandaCommand.");
		}

		configuration = new PandaCommandConfiguration(pandaCommand.id(), pandaCommand.pathName(),
				pandaCommand.defaultName(), pandaCommand.defaultDescription())
				.setHelpCommand(pandaCommand.helpCommand()).setDefaultPermission(pandaCommand.defaultPermission())
				.setDefaultUsage(pandaCommand.defaultUsage()).setDefaultAliases(pandaCommand.defaultAliases())
				.setCooldown(pandaCommand.cooldown());

		if (pandaCommandCustomizable != null) {
			configuration.setCustomizableName(pandaCommandCustomizable.name())
					.setCustomizableDescription(pandaCommandCustomizable.description())
					.setCustomizablePermission(pandaCommandCustomizable.permission())
					.setCustomizableUsage(pandaCommandCustomizable.usage())
					.setCustomizableAliases(pandaCommandCustomizable.aliases())
					.setCustomizableCooldown(pandaCommandCustomizable.cooldown());
		} else {
			configuration.setCustomizableName(false);
			configuration.setCustomizableDescription(false);
		}

		initFields();
	}

	public PandaSubCommand(@NonNull PandaCommandConfiguration configuration) {
		this.configuration = configuration;

		initFields();
	}

	public boolean precondition() {
		return true;
	}

	public void initFields() {
		this.nameField = new PandaStringField(String.format("%s.%s", getFieldPath(), "Name"),
				configuration.getDefaultName());

		this.descriptionField = new PandaStringField(String.format("%s.%s", getFieldPath(), "Description"),
				configuration.getDefaultDescription());

		this.permissionField = new PandaStringField(String.format("%s.%s", getFieldPath(), "Permission"),
				configuration.getDefaultPermission());

		this.usageField = new PandaStringField(String.format("%s.%s", getFieldPath(), "Usage"),
				configuration.getDefaultUsage());

		this.aliasesField = new PandaStringListField(String.format("%s.%s", getFieldPath(), "Aliases"),
				Arrays.asList(configuration.getDefaultAliases()));

		this.cooldownField = new PandaLongField(String.format("%s.%s", getFieldPath(), "Cooldown"),
				configuration.getCooldown());
	}

	public HashMap<String, List<PandaField<?>>> getCustomizableFileFields() {
		List<PandaField<?>> fields = new LinkedList<>();

		if (configuration.isCustomizableName()) {
			fields.add(nameField);
		}

		if (configuration.isCustomizableDescription()) {
			fields.add(descriptionField);
		}

		if (configuration.isCustomizablePermission()) {
			fields.add(permissionField);
		}

		if (configuration.isCustomizableUsage()) {
			fields.add(usageField);
		}

		if (configuration.isCustomizableAliases()) {
			fields.add(aliasesField);
		}

		if (configuration.isCustomizableCooldown()) {
			fields.add(cooldownField);
		}

		HashMap<String, List<PandaField<?>>> fileFields = new HashMap<>();
		fileFields.put("commands", fields);

		return fileFields;
	}

	public String getCurrentName() {
		return nameField.getContent();
	}

	public String getCurrentDescription() {
		return descriptionField.getContent();
	}

	public String getCurrentPermission() {
		return permissionField.getContent() != null ? permissionField.getContent() : "";
	}

	public String getCurrentUsage() {
		return usageField.getContent() != null ? usageField.getContent() : "";
	}

	public List<String> getCurrentAliases() {
		return aliasesField.getContent() != null ? aliasesField.getContent() : Collections.emptyList();
	}

	public Long getCurrentCooldown() {
		return cooldownField.getContent() != null ? cooldownField.getContent() : 0;
	}

	public String getId() {
		return this.configuration.getId();
	}

	public String getFieldPath() {
		return String.format("%s.%s",
				(this.parent != null ? String.format("%s.Subcommands", this.parent.getFieldPath()) : "Commands"),
				this.configuration.getPathName());
	}

	public String getIdPath() {
		return String.format("%s%s", (parent != null ? parent.getIdPath() + " " : ""), getId());
	}

	public String getCommandPath() {
		return String.format("%s%s", (parent != null ? parent.getCommandPath() + " " : ""), getCurrentName());
	}

	public String getCommandUsage() {
		return "/" + getCommandPath() + (!getCurrentUsage().isEmpty() ? " " + getCurrentUsage() : "");
	}

	public List<String> getRegisteredKeyOnlyParameters() {
		return Collections.emptyList();
	}

	public List<String> getRegisteredKeyValueParameters() {
		return Collections.emptyList();
	}

	public boolean hasPendingCommand(CommandSender sender) {
		return this.queue.getCooldownStatus(sender) == Status.PENDING;
	}

	public boolean executeCommand(CommandSender sender, String alias, String[] args) {
		if (!precondition()) {
			return false;
		}

		if (!getCurrentPermission().isEmpty() && !sender.hasPermission(getCurrentPermission())) {
			return false;
		}

		if (this.parent == null && hasPendingCommand(sender)) {
			MessageTemplate.inst(ERROR_COMMANDS_PENDINGCOMMAND.applyPrefix()).process().sendMessage(sender);
			return true;
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
		queue.addCooldown(sender, response);
		return response instanceof FalseResponse ? false : true;
	}

	protected CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		return executeCommandProcessLegacy(sender, parameters) ? new TrueResponse() : new FalseResponse();
	}

	protected boolean executeCommandProcessLegacy(CommandSender sender, PandaParameters parameters) {
		return false;
	}

	/*
	 * Autocomplete methods
	 */

	private Map<UUID, Map<Integer, Pair<Long, List<String>>>> cachedAutocompleteLists = new HashMap<>();

	public List<String> getAutocompleteList(Player sender, String[] args) {
		if (args.length == 0) {
			return Collections.emptyList();
		}

		String lastArg = args[args.length - 1].toLowerCase();

		return getCachedAutocompleteList((Player) sender, args.length - 1).stream()
				.filter(value -> value.toLowerCase().contains(lastArg)).collect(Collectors.toList());
	}

	public List<String> getCachedAutocompleteList(Player sender, int argIndex) {
		return cachedAutocompleteLists.computeIfAbsent(((Player) sender).getUniqueId(), (uuid) -> new HashMap<>())
				.computeIfAbsent(argIndex,
						(index) -> Pair.of(System.currentTimeMillis(), generateAutocompleteList(sender, argIndex)))
				.getSecond();
	}

	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		return Collections.emptyList();
	}

	public void clearCachedAutocompleteLists() {
		cachedAutocompleteLists.values().removeIf(value -> {
			value.values().removeIf(pair -> System.currentTimeMillis() - pair.getFirst() > 10000);
			return value.isEmpty();
		});
	}

}
