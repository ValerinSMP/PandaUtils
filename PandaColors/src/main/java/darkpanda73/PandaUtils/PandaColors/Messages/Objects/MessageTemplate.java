package darkpanda73.PandaUtils.PandaColors.Messages.Objects;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;

import lombok.Getter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public class MessageTemplate {

	private static final MessageBuilder MESSAGE_BUILDER;

	static {
		MessageBuilder builder = null;

		try {
			Matcher matcher = Pattern.compile("^([0-9\\.]+)\\-.*$").matcher(Bukkit.getBukkitVersion());
			if (matcher.find()) {
				String[] versionInfo = matcher.group(1).split("\\.");

				if (versionInfo.length >= 2) {
					int majorVersion = Integer.parseInt(versionInfo[0]);
					int minorVersion = Integer.parseInt(versionInfo[1]);

					if (majorVersion >= 26) {
						builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.Modern.ModernMessageBuilder();
					} else if (majorVersion == 1) {
						if (minorVersion >= 11) {
							builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_11_R1.NMSMessageBuilder();
						} else if (minorVersion >= 9) {
							builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_9_R2.NMSMessageBuilder();
						}
					}
				}
			}

			if (builder == null) {
				double version = 0D;
				try {
					String specificVersion = Bukkit.getVersion();
					specificVersion = specificVersion.substring(specificVersion.indexOf("(MC: ") + 5,
							specificVersion.indexOf(')'));
					version = Double.parseDouble(specificVersion.substring(2));
				} catch (Exception e) {
					e.printStackTrace();
				}

				if (version >= 8D) {
					if (version < 8.4D) {
						builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_8_R1.NMSMessageBuilder();
					} else if (version < 9.4D) {
						builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_8_R3.NMSMessageBuilder();
					} else if (version < 11D) {
						builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_9_R2.NMSMessageBuilder();
					} else {
						builder = new darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_11_R1.NMSMessageBuilder();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		MESSAGE_BUILDER = builder != null ? builder
				: new darkpanda73.PandaUtils.PandaColors.Messages.NMS.Default.NMSMessageBuilder();
	}

	public static MessageBuilder getMessageBuilder() {
		return MESSAGE_BUILDER;
	}

	private @Getter String[] text = new String[0];
	private @Getter Replacement[] replacements = new Replacement[0];
	private @Getter boolean color = true;

	public MessageTemplate setText(List<String> text) {
		return setText(text.toArray(new String[text.size()]));
	}

	public MessageTemplate setText(String... text) {
		this.text = text != null ? text : new String[0];
		return this;
	}

	public MessageTemplate setReplacements(Replacement... replacements) {
		this.replacements = replacements != null ? replacements : new Replacement[0];
		return this;
	}

	public MessageTemplate setColor(boolean color) {
		this.color = color;
		return this;
	}

	public Message process() {
		return MESSAGE_BUILDER.createMessage(this);
	}

	public List<String> getStrings() {
		return process().getStrings();
	}

	@Override
	public String toString() {
		return process().toString();
	}

	public static MessageTemplate inst(List<String> text) {
		return new MessageTemplate().setText(text);
	}

	public static MessageTemplate inst(String... text) {
		return new MessageTemplate().setText(text);
	}

	public static MessageTemplate inst() {
		return new MessageTemplate();
	}

}
