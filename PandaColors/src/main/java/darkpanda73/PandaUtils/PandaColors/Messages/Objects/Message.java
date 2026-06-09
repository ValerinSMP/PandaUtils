package darkpanda73.PandaUtils.PandaColors.Messages.Objects;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import darkpanda73.PandaUtils.PandaColors.Enums.ChatMessageType;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;

public class Message {

	private @Getter List<List<? extends BaseComponent>> components;
	private BaseComponent[] componentsArray;
	private List<String> lines;
	private @Getter @Setter long timeRequired;

	public Message(List<List<? extends BaseComponent>> components, List<String> lines) {
		this.components = components;
		List<TextComponent> textComponents = this.components.stream()
				.map(list -> list.toArray(new BaseComponent[list.size()])).map(array -> new TextComponent(array))
				.collect(Collectors.toList());
		this.componentsArray = textComponents.toArray(new BaseComponent[textComponents.size()]);
		this.lines = lines;
	}

	public void sendMessage(CommandSender... senders) {
		MessageTemplate.getMessageBuilder().sendMessage(senders, ChatMessageType.CHAT, this);
	}

	public void sendTitle(CommandSender... senders) {
		sendTitle(20, 60, 20, senders);
	}

	public void sendTitle(int fadein, int stay, int fadeout, CommandSender... senders) {
		if (getStrings().size() > 0) {
			for (CommandSender sender : senders) {
				if (sender instanceof Player) {
					MessageTemplate.getMessageBuilder().sendTitle((Player) sender, getStrings().get(0),
							getStrings().size() > 1 ? getStrings().get(1) : null, fadein, stay, fadeout);
				}
			}
		}
	}

	public void sendActionBar(CommandSender... senders) {
		MessageTemplate.getMessageBuilder().sendMessage(senders, ChatMessageType.ACTION_BAR, this);
	}

	public void sendSystem(CommandSender... senders) {
		MessageTemplate.getMessageBuilder().sendMessage(senders, ChatMessageType.SYSTEM, this);
	}

	public void broadcast() {
		broadcast(true);
	}

	public void broadcast(boolean includeConsole) {
		List<CommandSender> senders = new ArrayList<>();
		for (Player pl : Bukkit.getOnlinePlayers()) {
			senders.add(pl);
		}

		if (includeConsole) {
			senders.add(Bukkit.getConsoleSender());
		}
		sendMessage(senders.toArray(new CommandSender[senders.size()]));
	}

	@Override
	public String toString() {
		return lines.stream().collect(Collectors.joining("\n§r"));
	}

	public List<String> getStrings() {
		return this.lines;
	}

	public BaseComponent[] asComponentsArray() {
		return this.componentsArray;
	}

}
