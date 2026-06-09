package darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_8_R3;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import darkpanda73.PandaUtils.PandaColors.Messages.Utils.ComponentUtilities;
import net.md_5.bungee.api.chat.BaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;

public class NMSMessageBuilder extends darkpanda73.PandaUtils.PandaColors.Messages.NMS.Base.BaseMessageBuilder {

	@Override
	public void sendMessage(CommandSender[] senders, ChatMessageType type, Message message) {
		List<BaseComponent[]> components = message.getComponents().stream()
				.map(list -> list.toArray(new BaseComponent[list.size()])).collect(Collectors.toList());

		for (CommandSender sender : senders) {
			Player pl = sender instanceof Player ? (Player) sender : null;
			if (pl != null) {
				final PacketPlayOutChat packet = new PacketPlayOutChat(null,
						(byte) ComponentUtilities.toBungeeMessageType(type).ordinal());
				components.forEach(array -> {
					packet.components = array;
					((CraftPlayer) pl).getHandle().playerConnection.sendPacket(packet);
				});
			} else {
				for (String line : message.getStrings()) {
					sender.sendMessage(line);
				}
			}
		}
	}

}
