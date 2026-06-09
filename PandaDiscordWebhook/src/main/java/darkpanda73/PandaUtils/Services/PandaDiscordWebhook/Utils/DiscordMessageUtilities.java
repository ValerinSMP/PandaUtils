package darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Utils;

import java.awt.Color;
import java.util.concurrent.atomic.AtomicInteger;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Author;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Field;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Footer;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Image;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessage.EmbedObject.Thumbnail;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageTemplate;

public class DiscordMessageUtilities {

	public static DiscordMessage sectionToTemplate(YamlSection section) {
		DiscordMessage message = new DiscordMessage();

		if (section.has("username")) {
			message.setUsername(section.getData("username").getString());
		}

		if (section.has("avatar-url")) {
			message.setAvatarUrl(section.getData("avatar-url").getString());
		}

		if (section.has("content")) {
			message.setContent(section.getData("content").getStringList());
		}

		if (section.has("embeds")) {
			section.getSection("embeds").getNamedChilds().forEach(child -> {
				if (child.isYamlSection()) {
					message.addEmbed(sectionToEmbed(child.asYamlSection()));
				}
			});
		}

		return message;
	}

	public static EmbedObject sectionToEmbed(YamlSection section) {
		EmbedObject embed = new EmbedObject();

		if (section.has("title")) {
			embed.setTitle(section.getData("title").getString());
		}

		if (section.has("description")) {
			embed.setDescription(section.getData("description").getStringList());
		}

		if (section.has("url")) {
			embed.setUrl(section.getData("url").getString());
		}

		if (section.has("color")) {
			embed.setColor(Color.decode(section.getData("color").getString()));
		}

		if (section.has("image-url")) {
			embed.setImage(new Image(section.getData("image-url").getString()));
		}

		if (section.has("thumbnail-url")) {
			embed.setThumbnail(new Thumbnail(section.getData("thumbnail-url").getString()));
		}

		if (section.has("author")) {
			embed.setAuthor(sectionToAuthor(section.getSection("author")));
		}

		if (section.has("fields")) {
			section.getSection("fields").getNamedChilds().forEach(child -> {
				if (child.isYamlSection()) {
					embed.addField(sectionToField(child.asYamlSection()));
				}
			});
		}

		return embed;
	}

	public static Author sectionToAuthor(YamlSection section) {
		Author author = new Author();

		if (section.has("name")) {
			author.setName(section.getData("name").getString());
		}

		if (section.has("url")) {
			author.setUrl(section.getData("url").getString());
		}

		if (section.has("icon-url")) {
			author.setIconUrl(section.getData("icon-url").getString());
		}

		return author;
	}

	public static Footer sectionToFooter(YamlSection section) {
		Footer footer = new Footer();

		if (section.has("text")) {
			footer.setText(section.getData("text").getString());
		}

		if (section.has("icon-url")) {
			footer.setIconUrl(section.getData("icon-url").getString());
		}

		return footer;
	}

	public static Field sectionToField(YamlSection section) {
		Field field = new Field();

		if (section.has("name")) {
			field.setName(section.getData("name").getString());
		}

		if (section.has("value")) {
			field.setValue(section.getData("value").getStringList());
		}

		if (section.has("inline")) {
			field.setInline(section.getData("inline").getBoolean());
		}

		return field;
	}

	public static YamlSection templateToSection(DiscordMessageTemplate template) {
		YamlSection section = new YamlSection(template.getId());

		if (template.getDefaultMessage().getUsername() != null) {
			section.set("Username", template.getDefaultMessage().getUsername());
		}

		if (template.getDefaultMessage().getAvatarUrl() != null) {
			section.set("Avatar-url", template.getDefaultMessage().getAvatarUrl());
		}

		if (template.getDefaultMessage().getContent() != null) {
			section.set("Content", template.getDefaultMessage().getContent());
		}

		if (template.getDefaultMessage().getEmbeds() != null && !template.getDefaultMessage().getEmbeds().isEmpty()) {
			AtomicInteger index = new AtomicInteger(1);
			template.getDefaultMessage().getEmbeds().forEach(embed -> {
				embedToSection(embed).toMap()
						.forEach((key, value) -> section.set("Embeds." + index.get() + "." + key, value));
				index.incrementAndGet();
			});
		}

		return section;
	}

	public static YamlSection embedToSection(EmbedObject embed) {
		YamlSection section = new YamlSection("Embed");

		if (embed.getTitle() != null) {
			section.set("Title", embed.getTitle());
		}

		if (embed.getDescription() != null) {
			section.set("Description", embed.getDescription());
		}

		if (embed.getUrl() != null) {
			section.set("Url", embed.getUrl());
		}

		if (embed.getColor() != null) {
			section.set("Color", String.format("#%02x%02x%02x", embed.getColor().getRed(), embed.getColor().getGreen(),
					embed.getColor().getBlue()));
		}

		if (embed.getImage() != null) {
			section.set("Image-url", embed.getImage().getUrl());
		}

		if (embed.getThumbnail() != null) {
			section.set("Thumbnail-url", embed.getThumbnail().getUrl());
		}

		if (embed.getAuthor() != null) {
			authorToSection(embed.getAuthor()).toMap().forEach((key, value) -> section.set("Author." + key, value));
		}

		if (embed.getFooter() != null) {
			footerToSection(embed.getFooter()).toMap().forEach((key, value) -> section.set("Footer." + key, value));
		}

		if (embed.getFields() != null && !embed.getFields().isEmpty()) {
			AtomicInteger index = new AtomicInteger(1);
			embed.getFields().forEach(field -> {
				fieldToSection(field).toMap()
						.forEach((key, value) -> section.set("Fields." + index.get() + "." + key, value));
				index.incrementAndGet();
			});
		}

		return section;
	}

	public static YamlSection authorToSection(Author author) {
		YamlSection section = new YamlSection("Author");

		if (author.getName() != null) {
			section.set("Name", author.getName());
		}

		if (author.getUrl() != null) {
			section.set("Url", author.getUrl());
		}

		if (author.getIconUrl() != null) {
			section.set("Icon-url", author.getIconUrl());
		}

		return section;
	}

	public static YamlSection footerToSection(Footer footer) {
		YamlSection section = new YamlSection("Footer");

		if (footer.getText() != null) {
			section.set("Text", footer.getText());
		}

		if (footer.getIconUrl() != null) {
			section.set("Icon-url", footer.getIconUrl());
		}

		return section;
	}

	public static YamlSection fieldToSection(Field field) {
		YamlSection section = new YamlSection("Field");

		if (field.getName() != null) {
			section.set("Name", field.getName());
		}

		if (field.getValue() != null) {
			section.set("Value", field.getValue());
		}

		if (field.getInline() != null) {
			section.set("Inline", field.getInline());
		}

		return section;
	}

}
