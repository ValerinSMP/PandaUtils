package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.ContentDisposition;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.Application;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.Image;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Multipart;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.Text;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@RequiredArgsConstructor
@Setter(lombok.AccessLevel.NONE)
public abstract class Content {

	protected @NonNull ContentType contentType;
	protected ContentDisposition contentDisposition = ContentDisposition.ATTACHMENT;

	public Multipart asMultipart() {
		return (Multipart) this;
	}

	public Application asApplication() {
		return (Application) this;
	}

	public Text asText() {
		return (Text) this;
	}

	public Image asImage() {
		return (Image) this;
	}

	public boolean isMultipart() {
		return this.contentType == ContentType.MULTIPART;
	}

	public boolean isApplication() {
		return this.contentType == ContentType.APPLICATION;
	}

	public boolean isText() {
		return this.contentType == ContentType.TEXT;
	}

	public boolean isImage() {
		return this.contentType == ContentType.IMAGE;
	}

	public boolean isEmpty() {
		return this.contentType == ContentType.EMPTY;
	}

	public static enum ContentType {
		MULTIPART, APPLICATION, TEXT, IMAGE, EMPTY
	}

	public abstract byte[] toBytes();

	public abstract String getContentTypeHeader();

}
