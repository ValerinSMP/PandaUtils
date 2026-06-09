package darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.bukkit.OfflinePlayer;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONArray;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;
import net.md_5.bungee.api.ChatColor;

@Data
@Accessors(chain = true)
public class DiscordMessageInstance {

	private DiscordMessageTemplate template;
	private OfflinePlayer executor;
	private Replacement[] replacements = new Replacement[0];
	private List<Attachment> attachments = new ArrayList<>();

	public DiscordMessageInstance(DiscordMessageTemplate template, Replacement... replacements) {
		this(template, null, replacements);
	}

	public DiscordMessageInstance(DiscordMessageTemplate template, OfflinePlayer executor,
			Replacement... replacements) {
		this.template = template;
		this.executor = executor;

		Replacement[] customReplacements = new Replacement[] {
				new Replacement("{executor_name}", () -> executor != null ? executor.getName() : "Console"),
				new Replacement("{executor_icon_url}",
						() -> executor != null ? String.format("https://mineskin.eu/helm/%s", executor.getName())
								: "") };
		this.replacements = ArrayUtilities.join(new Replacement[replacements.length + customReplacements.length],
				replacements, customReplacements);
	}

	public DiscordMessageInstance addAttachments(Attachment... attachments) {
		for (Attachment attachment : attachments) {
			this.attachments.add(attachment);
		}
		return this;
	}

	public String process() {
		PandaJSONObject jsonObject = this.template.getMessage().toJsonObject();

		if (!this.attachments.isEmpty()) {
			PandaJSONArray attachmentsObject = new PandaJSONArray();

			AtomicInteger index = new AtomicInteger();
			this.attachments.forEach(attachment -> {
				PandaJSONObject attachmentObject = new PandaJSONObject();
				attachmentObject.put("id", index.getAndIncrement());
				attachmentObject.put("description", attachment.getDescription());
				attachmentObject.put("filename", attachment.getFilename());
				attachmentsObject.add(attachmentObject);
			});

			jsonObject.put("attachments", attachmentsObject);
		}

		AtomicReference<String> json = new AtomicReference<>(jsonObject.toString());

		Arrays.asList(replacements).forEach(replacement -> {
			json.set(json.get().replace(replacement.getTextToReplace(),
					ChatColor.stripColor(replacement.getMessageFragment().getText())));
		});

		return json.get();
	}

	@Data
	@AllArgsConstructor
	public static abstract class Attachment {

		private String filename;
		private String description;

		public abstract String getContentType();

		public abstract byte[] getContent();

		public static class TxtAttachment extends Attachment {

			private String content;

			public TxtAttachment(String filename, String description, String content) {
				super(filename, description);
				this.content = content;
			}

			@Override
			public String getContentType() {
				return "text/plain";
			}

			public byte[] getContent() {
				return this.content.getBytes();
			}

		}

	}

}
