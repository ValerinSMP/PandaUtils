package darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Services;

import org.bukkit.Bukkit;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Listeners.PandaMessageListener;
import lombok.Getter;

@PandaService(loadOn = LoadStep.ENABLE)
public class PandaMessageListenerService {

	@RegisteredPandaField("config")
	private static final PandaStringField SETTINGS_MESSAGELISTENER_LISTENERPRIORITY = new PandaStringField(
			"Settings.Message-listener.Listener-priority", EventPriority.LOWEST.name());

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private @Getter PandaMessageListener listener = new PandaMessageListener();

	@PostInjectMethod
	private void postInject() {
		PandaYamlFile file = filesService.getYaml("config");
		if (file != null) {
			file.registerFieldsFromClass(PandaMessageListenerService.class);
			file.registerFieldsFromClass(PandaMessageListener.class);
		}
	}

	@LoadMethod
	@ReloadMethod
	private void load() {
		EventPriority priority;

		try {
			priority = EventPriority.valueOf(SETTINGS_MESSAGELISTENER_LISTENERPRIORITY.getContent().toUpperCase());
		} catch (IllegalArgumentException e) {
			priority = EventPriority.LOWEST;
		}

		HandlerList.unregisterAll(listener);
		Bukkit.getPluginManager().registerEvents(listener, plugin);
		Bukkit.getPluginManager().registerEvent(AsyncPlayerChatEvent.class, listener, priority, (l, e) -> {
			if (!(e instanceof AsyncPlayerChatEvent)) {
				return;
			}

			listener.onPlayerChatEvent((AsyncPlayerChatEvent) e);
		}, plugin, false);
	}

}
