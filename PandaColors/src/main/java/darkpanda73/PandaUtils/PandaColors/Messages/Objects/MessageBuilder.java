package darkpanda73.PandaUtils.PandaColors.Messages.Objects;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;

public interface MessageBuilder {

	public default void sendTitle(Player player, MessageTemplate title, MessageTemplate subtitle) {
		this.sendTitle(player, title, subtitle, 20, 60, 20);
	}

	public default void sendTitle(Player player, MessageTemplate title, MessageTemplate subtitle, int fadein, int stay,
			int fadeout) {
		this.sendTitle(player, title.process().toString(), subtitle.process().toString(), fadein, stay, fadeout);
	}

	public abstract void sendTitle(Player player, String title, String subtitle, int fadein, int stay, int fadeout);

	public abstract void sendMessage(CommandSender[] senders, ChatMessageType type, Message message);

	public abstract Message createMessage(MessageTemplate input);

}
