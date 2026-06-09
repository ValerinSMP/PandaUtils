package darkpanda73.PandaUtils.PandaColors.Messages.NMS.Default;

import org.bukkit.command.CommandSender;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import darkpanda73.PandaUtils.PandaColors.Messages.NMS.Base.BaseMessageBuilder;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;

public class NMSMessageBuilder extends BaseMessageBuilder {

	@Override
	public void sendMessage(CommandSender[] senders, ChatMessageType type, Message result) {
		for (CommandSender sender : senders) {
			for (String line : result.getStrings()) {
				sender.sendMessage(line);
			}
		}
	}

}
