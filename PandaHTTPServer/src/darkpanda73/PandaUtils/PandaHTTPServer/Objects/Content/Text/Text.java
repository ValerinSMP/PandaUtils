package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public abstract class Text extends Content {

	private @NonNull String content;
	private @NonNull TextType textType;

	public Text(TextType textType, String content) {
		super(ContentType.TEXT);
		this.textType = textType;
		this.content = content;
	}

	@Override
	public String toString() {
		return content;
	}

	@Override
	public byte[] toBytes() {
		return toString().getBytes();
	}

	@Override
	public String getContentTypeHeader() {
		return "text/" + textType.getHeader();
	}

	@AllArgsConstructor
	public static enum TextType {
		PLAIN("plain"), HTML("html"), CSS("css"), JAVASCRIPT("javascript");

		private String header;

		public String getHeader() {
			return this.header;
		}

		public static TextType getFromHeader(String header) {
			for (TextType type : values()) {
				if (type.getHeader().equalsIgnoreCase(header)) {
					return type;
				}
			}
			return null;
		}
	}

}
