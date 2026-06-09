package darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.PandaDiscordService;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Utils.DiscordMessageUtilities;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
public class DiscordMessageTemplate {

	@PandaInject
	private static PandaDiscordService discordService;

	private @Setter(value = AccessLevel.NONE) String id;
	private @Setter(value = AccessLevel.NONE) DiscordMessage defaultMessage;
	private boolean enabled;
	private String webhookUrl;
	private DiscordMessage message;

	public DiscordMessageTemplate(String id, DiscordMessage defaultMessage) {
		this.id = id;
		this.defaultMessage = defaultMessage;
		this.message = defaultMessage;
	}

	public void updateMessage(YamlSection section) {
		this.message = DiscordMessageUtilities.sectionToTemplate(section);
	}

	public boolean isEnabled() {
		return this.enabled && PandaDiscordService.DISCORD_ENABLE.isTrue();
	}

	public String getWebhookUrl() {
		return this.webhookUrl != null && !this.webhookUrl.isEmpty() ? this.webhookUrl
				: PandaDiscordService.DISCORD_WEBHOOKURL.getContent();
	}

	public boolean isWebhookUrlDefined() {
		return (this.webhookUrl != null && !this.webhookUrl.isEmpty()) || discordService.isGeneralWebhookDefined();
	}

}
