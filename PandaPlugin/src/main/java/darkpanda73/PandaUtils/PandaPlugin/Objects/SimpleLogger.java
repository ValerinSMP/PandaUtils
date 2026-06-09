package darkpanda73.PandaUtils.PandaPlugin.Objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SimpleLogger {

	private @Getter(lombok.AccessLevel.PROTECTED) PandaPluginClass plugin;

	public void sendDebug(String... messages) {
		sendDebug(null, messages);
	}

	public void sendDebug(Class<?> originClass, String... messages) {
		// Don't send debug messages as now for this, it's required to use the
		// PandaLoggingModule

//		MessageTemplate
//				.inst(Arrays.stream(messages)
//						.map(message -> plugin.getPrefix()
//								+ String.format("%s",
//										originClass != null ? String.format("[%s] ", originClass.getSimpleName()) : "")
//								+ message)
//						.collect(Collectors.toList()))
//				.process().sendMessage(Bukkit.getConsoleSender());
	}

	public void sendInfo(String... messages) {
		sendInfo(null, messages);
	}

	public void sendInfo(Class<?> originClass, String... messages) {
		MessageTemplate
				.inst(Arrays.stream(messages)
						.map(message -> plugin.getPrefix()
								+ String.format("%s",
										originClass != null ? String.format("[%s] ", originClass.getSimpleName()) : "")
								+ message)
						.collect(Collectors.toList()))
				.process().sendMessage(Bukkit.getConsoleSender());
	}

	public void sendWarning(String... messages) {
		sendWarning(null, messages);
	}

	public void sendWarning(Class<?> originClass, String... messages) {
		MessageTemplate
				.inst(Arrays.stream(messages)
						.map(message -> plugin.getPrefix() + String.format("&e%s",
								originClass != null ? String.format("&e[%s] ", originClass.getSimpleName()) : "")
								+ message)
						.collect(Collectors.toList()))
				.process().sendMessage(Bukkit.getConsoleSender());
	}

	public void sendError(String... messages) {
		sendError(null, messages);
	}

	public void sendError(Class<?> originClass, String... messages) {
		MessageTemplate
				.inst(Arrays.stream(messages)
						.map(message -> plugin.getPrefix() + String.format("&c%s",
								originClass != null ? String.format("&c[%s] ", originClass.getSimpleName()) : "")
								+ message)
						.collect(Collectors.toList()))
				.process().sendMessage(Bukkit.getConsoleSender());
	}

}
