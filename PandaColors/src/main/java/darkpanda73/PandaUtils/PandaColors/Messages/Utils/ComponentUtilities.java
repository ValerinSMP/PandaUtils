package darkpanda73.PandaUtils.PandaColors.Messages.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class ComponentUtilities {

	private static Map<ChatMessageType, net.md_5.bungee.api.ChatMessageType> messageTypeToBungeeMessageType;

	static {
		messageTypeToBungeeMessageType = new HashMap<>();

		for (ChatMessageType type : ChatMessageType.values()) {
			try {
				messageTypeToBungeeMessageType.put(type, net.md_5.bungee.api.ChatMessageType.valueOf(type.name()));
			} catch (IllegalArgumentException e) {
				messageTypeToBungeeMessageType.put(type, net.md_5.bungee.api.ChatMessageType.values()[0]);
			}
		}
	}

	public static net.md_5.bungee.api.ChatMessageType toBungeeMessageType(ChatMessageType messageType) {
		return messageTypeToBungeeMessageType.get(messageType);
	}

	public static String toLegacyText(List<? extends BaseComponent> components) {
		String result = ColorUtilities.stripColorExceed(components.stream().map(component -> {
			String legacy = TextComponent.toLegacyText(component);

			int startIndex = 0;
			if (component.getColorRaw() == null) {
				while (startIndex + 1 < legacy.length() && legacy.charAt(startIndex) == '§'
						&& legacy.charAt(startIndex + 1) == 'f') {
					startIndex += 2;
				}
			}

			return legacy.substring(startIndex);
		}).collect(Collectors.joining()));

		if (components.size() > 0) {
			BaseComponent component = components.get(0);
			if (component.getExtra().size() > 0 && component.getExtra().get(0).getColorRaw() == ChatColor.WHITE) {
				return ChatColor.WHITE + result;
			}
		}

		return result;
	}

}
