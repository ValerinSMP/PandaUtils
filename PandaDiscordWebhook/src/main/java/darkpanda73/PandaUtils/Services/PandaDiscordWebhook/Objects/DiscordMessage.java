package darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects;

import java.awt.Color;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONArray;
import darkpanda73.PandaUtils.PandaJSON.Objects.PandaJSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class DiscordMessage {

	public static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	private static final String LINE_SEPARATOR = System.lineSeparator().replaceAll("(\\r|\\n|\\r\\n)+", "\\\\n");

	private String username;
	private String avatarUrl;
	private List<String> content;

	private @Setter(lombok.AccessLevel.NONE) List<EmbedObject> embeds = new ArrayList<>();

	public DiscordMessage addEmbed(EmbedObject embed) {
		this.embeds.add(embed);
		return this;
	}

	public PandaJSONObject toJsonObject() {
		PandaJSONObject json = new PandaJSONObject();

		if (this.username != null) {
			json.put("username", this.username);
		}

		if (this.avatarUrl != null) {
			json.put("avatar_url", this.avatarUrl);
		}

		if (this.content != null) {
			json.put("content", this.content.stream().collect(Collectors.joining(LINE_SEPARATOR)));
		}

		if (!this.embeds.isEmpty()) {
			PandaJSONArray array = new PandaJSONArray();
			embeds.forEach(embed -> array.add(embed.toJsonObject()));
			json.put("embeds", array);
		}

		return json;
	}

	@Data
	@Accessors(chain = true)
	public static class EmbedObject {

		private String title;
		private List<String> description;
		private String url;
		private Color color;

		private Author author;
		private Image image;
		private Thumbnail thumbnail;
		private Footer footer;

		private @Setter(lombok.AccessLevel.NONE) List<Field> fields = new ArrayList<>();

		public EmbedObject addField(Field field) {
			this.fields.add(field);
			return this;
		}

		public EmbedObject addField(String name, List<String> value) {
			this.fields.add(new Field(name, value));
			return this;
		}

		public EmbedObject addField(String name, List<String> value, boolean inline) {
			this.fields.add(new Field(name, value, inline));
			return this;
		}

		public PandaJSONObject toJsonObject() {
			PandaJSONObject jsonEmbed = new PandaJSONObject();

			jsonEmbed.put("title", title);
			jsonEmbed.put("description", description.stream().collect(Collectors.joining(LINE_SEPARATOR)));
			jsonEmbed.put("url", url);

			if (color != null) {
				int rgb = color.getRed();
				rgb = (rgb << 8) + color.getGreen();
				rgb = (rgb << 8) + color.getBlue();

				jsonEmbed.put("color", rgb);
			}

			if (footer != null) {
				jsonEmbed.put("footer", footer.toJsonObject());
			}

			if (image != null) {
				jsonEmbed.put("image", image.toJsonObject());
			}

			if (thumbnail != null) {
				jsonEmbed.put("thumbnail", thumbnail.toJsonObject());
			}

			if (author != null) {
				jsonEmbed.put("author", author.toJsonObject());
			}

			PandaJSONArray array = new PandaJSONArray();
			fields.forEach(field -> array.add(field.toJsonObject()));
			jsonEmbed.put("fields", array);

			return jsonEmbed;
		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Author {

			private String name;
			private String url;
			private String iconUrl;

			public PandaJSONObject toJsonObject() {
				PandaJSONObject json = new PandaJSONObject();
				json.put("name", name);

				if (url != null) {
					json.put("url", url);
				}

				if (iconUrl != null) {
					json.put("icon_url", iconUrl);
				}
				return json;
			}

		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Footer {

			private String text;
			private String iconUrl;
			private Date timestamp;

			public PandaJSONObject toJsonObject() {
				PandaJSONObject json = new PandaJSONObject();
				json.put("text", text);

				if (iconUrl != null) {
					json.put("icon_url", iconUrl);
				}

				if (timestamp != null) {
					json.put("timestamp", DATE_FORMAT.format(timestamp));
				}

				return json;
			}

		}

		@Getter
		@AllArgsConstructor
		public static class Thumbnail {

			private String url;

			public PandaJSONObject toJsonObject() {
				PandaJSONObject json = new PandaJSONObject();
				json.put("url", url);
				return json;
			}

		}

		@Getter
		@AllArgsConstructor
		public static class Image {

			private String url;

			public PandaJSONObject toJsonObject() {
				PandaJSONObject json = new PandaJSONObject();
				json.put("url", url);
				return json;
			}

		}

		@Data
		@NoArgsConstructor
		@AllArgsConstructor
		public static class Field {

			private String name;
			private List<String> value;
			private Boolean inline;

			public Field(String name, List<String> value) {
				this.name = name;
				this.value = value;
			}

			public PandaJSONObject toJsonObject() {
				PandaJSONObject json = new PandaJSONObject();
				json.put("name", name);
				json.put("value", value.stream().collect(Collectors.joining(LINE_SEPARATOR)));

				if (inline != null) {
					json.put("inline", inline);
				}
				return json;
			}

		}
	}

}
