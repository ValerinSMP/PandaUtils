package darkpanda73.PandaUtils.PandaUpdateChecker.Events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaUpdateChecker.Service.PandaUpdateCheckerService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaPrefixedStringField;

@PandaListener
public class PandaUpdateCheckerListener implements Listener {

	@RegisteredPandaField("lang")
	private static PandaPrefixedStringField outdatedMessage = new PandaPrefixedStringField(
			"Message.Update-checker.Outdated-version",
			"&cHey! You're currently using an outdated version of the plugin! &8(&aCurrent: &e%current_version%&a, New: &e%new_version%&8)");

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaUpdateCheckerService updateCheckerService;

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (PandaUpdateCheckerService.SETTINGS_UPDATECHECKER_JOINMESSAGE.getContent() && e.getPlayer().isOp()
				&& updateCheckerService.isOutdated()) {
			MessageTemplate.inst(outdatedMessage.applyPrefix())
					.setReplacements(new Replacement("%current_version%", () -> plugin.getDescription().getVersion()),
							new Replacement("%new_version%", () -> updateCheckerService.getFetchedVersion()))
					.process().sendMessage(e.getPlayer());
		}
	}

}
