package darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_9_R2;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.ComponentUtilities;
import net.md_5.bungee.api.chat.BaseComponent;

public class NMSMessageBuilder extends darkpanda73.PandaUtils.PandaColors.Messages.NMS.Base.BaseMessageBuilder {

	@Override
	public void sendMessage(CommandSender[] senders, ChatMessageType type, Message result) {
		for (CommandSender sender : senders) {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				for (BaseComponent component : result.asComponentsArray()) {
					pl.spigot().sendMessage(ComponentUtilities.toBungeeMessageType(type), component);
				}
			} else {
				for (String line : result.getStrings()) {
					sender.sendMessage(line);
				}
			}
		}
	}

}
