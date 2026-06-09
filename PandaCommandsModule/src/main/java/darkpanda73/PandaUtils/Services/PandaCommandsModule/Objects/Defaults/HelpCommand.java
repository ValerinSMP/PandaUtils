package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Defaults;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageFragment.ClickEvent;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Annotations.PandaCommandAnnotation;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaParameters;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaSubCommand;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse;
import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.Response.CommandResponse.TrueResponse;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;

@PandaCommandAnnotation(
		id = "help",
		pathName = "Help",
		defaultName = "help",
		defaultDescription = "Get all the commands",
		defaultUsage = "[page]",
		defaultAliases = { "h" })
@PandaCommandAnnotation.Customizable(permission = true, usage = true, aliases = true)
public class HelpCommand extends PandaSubCommand {

	private static final String SECTION_PREFIX = "Message.Helper.%s";

	private static final List<String> DEFAULT_HEADER = Arrays.asList(" ",
			"&c・。・。・。・。・。・。・。%left_arrow%&r %current_page%/%max_page% %right_arrow%&c。・。・。・。・。・。・。・", " ");
	private static final List<String> DEFAULT_BODY = Arrays.asList("&6%command_usage%", "  &8%command_description%");
	private static final List<String> DEFAULT_FOOTER = Arrays.asList(" ", "&c・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・。・", " ");

	private static final String DEFAULT_UNAVAILABLE_LEFT_ARROW = "&r«";
	private static final String DEFAULT_UNAVAILABLE_RIGHT_ARROW = "&r»";
	private static final String DEFAULT_AVAILABLE_LEFT_ARROW = "&e«";
	private static final String DEFAULT_AVAILABLE_RIGHT_ARROW = "&e»";
	private static final String DEFAULT_ERROR_NUMBERS = "&cYou must specify a valid number!";

	private PandaStringListField headerField;
	private PandaStringListField bodyField;
	private PandaStringListField footerField;

	private PandaStringField unavailableLeftArrowField;
	private PandaStringField unavailableRightArrowField;
	private PandaStringField availableLeftArrowField;
	private PandaStringField availableRightArrowField;
	private PandaPrefixedStringField errorNumbersField;

	public HelpCommand() throws InstantiationException {
		super();

		this.headerField = new PandaStringListField(String.format(SECTION_PREFIX, "Header"), DEFAULT_HEADER);
		this.bodyField = new PandaStringListField(String.format(SECTION_PREFIX, "Body"), DEFAULT_BODY);
		this.footerField = new PandaStringListField(String.format(SECTION_PREFIX, "Footer"), DEFAULT_FOOTER);
		this.unavailableLeftArrowField = new PandaStringField(String.format(SECTION_PREFIX, "Unavailable-left-arrow"),
				DEFAULT_UNAVAILABLE_LEFT_ARROW);
		this.unavailableRightArrowField = new PandaStringField(String.format(SECTION_PREFIX, "Unavailable-right-arrow"),
				DEFAULT_UNAVAILABLE_RIGHT_ARROW);
		this.availableLeftArrowField = new PandaStringField(String.format(SECTION_PREFIX, "Available-left-arrow"),
				DEFAULT_AVAILABLE_LEFT_ARROW);
		this.availableRightArrowField = new PandaStringField(String.format(SECTION_PREFIX, "Available-right-arrow"),
				DEFAULT_AVAILABLE_RIGHT_ARROW);
		this.errorNumbersField = new PandaPrefixedStringField(String.format(SECTION_PREFIX, "Error.Not-a-number"),
				DEFAULT_ERROR_NUMBERS);
	}

	@Override
	public HashMap<String, List<PandaField<?>>> getCustomizableFileFields() {
		HashMap<String, List<PandaField<?>>> fileFields = super.getCustomizableFileFields();

		List<PandaField<?>> fields = new LinkedList<>();
		fields.add(availableLeftArrowField);
		fields.add(unavailableLeftArrowField);
		fields.add(availableRightArrowField);
		fields.add(unavailableRightArrowField);
		fields.add(headerField);
		fields.add(bodyField);
		fields.add(footerField);
		fields.add(errorNumbersField);

		fileFields.put("lang", fields);

		return fileFields;
	}

	@Override
	protected List<String> generateAutocompleteList(Player sender, int argIndex) {
		List<String> list = new ArrayList<>();
		switch (argIndex) {
		case 0:
			List<PandaSubCommand> available = new ArrayList<>();
			available.add(this);
			for (PandaSubCommand command : getParent().getCommands()) {
				if (command.getCurrentPermission().isEmpty() || sender.hasPermission(command.getCurrentPermission())) {
					available.add(command);
				}
			}
			int max = (int) ((available.size() / 4D) + 0.99D);
			for (int i = 0; i < max; i++) {
				list.add(String.valueOf(i + 1));
			}
			break;
		default:
			break;

		}
		return list;
	}

	@Override
	public CommandResponse executeCommandProcess(CommandSender sender, PandaParameters parameters) {
		try {
			String scmd = parameters.getCommandAlias();
			int page = parameters.getParameters().size() > 0
					&& (scmd.equalsIgnoreCase(getCurrentName()) || getCurrentAliases().contains(scmd))
							? Integer.parseInt(parameters.getParameters().get(0))
							: 1;

			List<PandaSubCommand> available = new ArrayList<>();
			for (PandaCommand command : getParent().getRelatedCommands()) {
				if (command.precondition()) {
					available.add(command);
				}
			}
			available.add(this);
			for (PandaSubCommand command : getParent().getCommands()) {
				if (command.precondition() && (command.getCurrentPermission().isEmpty()
						|| sender.hasPermission(command.getCurrentPermission()))) {
					available.add(command);
				}
			}

			int max = (int) ((available.size() / 4D) + 0.99D);

			if (page > max) {
				page = max;
			}

			for (String s : headerField.getContent()) {
				applyHelpHeaderFootPlaceholders(s, this, page, max).sendMessage(sender);
			}

			for (int i = 4 * (page - 1); i < 4 * page && i < available.size(); i++) {
				if (i < available.size()) {
					for (String s : bodyField.getContent()) {
						applyHelpBodyPlaceholders(s, available.get(i)).sendMessage(sender);
					}
				}
			}

			for (String s : footerField.getContent()) {
				applyHelpHeaderFootPlaceholders(s, this, page, max).sendMessage(sender);
			}
		} catch (NumberFormatException e) {
			MessageTemplate.inst(errorNumbersField.applyPrefix()).process().sendMessage(sender);
		}
		return new TrueResponse();
	}

	public Message applyHelpHeaderFootPlaceholders(String message, PandaSubCommand scmd, int currentpage, int maxpage) {
		return MessageTemplate.inst(message)
				.setReplacements(new Replacement("%current_page%", () -> String.valueOf(currentpage)),
						new Replacement("%max_page%", () -> String
								.valueOf(maxpage)),
						new Replacement("%left_arrow%",
								() -> (currentpage > 1 ? availableLeftArrowField : unavailableLeftArrowField)
										.getContent(),
								(currentpage > 1
										? new ClickEvent(ClickEvent.Action.RUN_COMMAND,
												"/" + scmd.getCommandPath() + " " + (currentpage - 1))
										: (ClickEvent) null)),
						new Replacement("%right_arrow%",
								() -> (currentpage < maxpage ? availableRightArrowField : unavailableRightArrowField)
										.getContent(),
								(currentpage < maxpage ? new ClickEvent(ClickEvent.Action.RUN_COMMAND,
										"/" + scmd.getCommandPath() + " " + (currentpage + 1)) : null)))
				.process();
	}

	public Message applyHelpBodyPlaceholders(String message, PandaSubCommand cmd) {
		return MessageTemplate.inst(message)
				.setReplacements(
						new Replacement("%command_usage%", () -> cmd.getCommandUsage(),
								new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, cmd.getCommandUsage())),
						new Replacement("%command_description%", () -> cmd.getCurrentDescription()),
						new Replacement("%command_name%", () -> cmd.getCurrentName()),
						new Replacement("%command_permission%", () -> cmd.getCurrentPermission()))
				.process();
	}

}
