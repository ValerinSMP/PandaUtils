package darkpanda73.PandaUtils.PandaColors.Messages.NMS.Modern;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Message;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

/** Keep chat components intact and use Paper's public API for temporary messages. */
public final class ModernMessageBuilder extends darkpanda73.PandaUtils.PandaColors.Messages.NMS.v1_11_R1.NMSMessageBuilder {
    @Override
    public void sendMessage(CommandSender[] senders, ChatMessageType type, Message result) {
        if (type != ChatMessageType.ACTION_BAR) {
            super.sendMessage(senders, type, result);
            return;
        }
        for (CommandSender sender : senders) {
            for (String line : result.getStrings()) {
                if (sender instanceof Player) {
                    ((Player) sender).sendActionBar(LegacyComponentSerializer.legacySection().deserialize(line));
                } else {
                    sender.sendMessage(line);
                }
            }
        }
    }
}
