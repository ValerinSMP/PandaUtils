package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public abstract class Application extends Content {

	private ApplicationType applicationType;
	private byte[] content;

	public Application(ApplicationType applicationType, byte[] content) {
		super(ContentType.APPLICATION);
		this.applicationType = applicationType;
		this.content = content;
	}

	@Override
	public byte[] toBytes() {
		return content;
	}

	@Override
	public String getContentTypeHeader() {
		return "application/" + applicationType.getHeader();
	}

	public boolean isPdf() {
		return this instanceof PdfApplication;
	}

	public boolean isOctetStream() {
		return this instanceof OctetStreamApplication;
	}

	public boolean isXwwwFormUrlEncoded() {
		return this instanceof XwwwFormUrlEncodedApplication;
	}

	public boolean isJson() {
		return this instanceof JsonApplication;
	}

	public PdfApplication asPdf() {
		return (PdfApplication) this;
	}

	public OctetStreamApplication asOctetStream() {
		return (OctetStreamApplication) this;
	}

	public XwwwFormUrlEncodedApplication asXwwwFormUrlEncoded() {
		return (XwwwFormUrlEncodedApplication) this;
	}

	public JsonApplication asJson() {
		return (JsonApplication) this;
	}

	@AllArgsConstructor
	public static enum ApplicationType {
		PDF("pdf"), OCTET_STREAM("octet-stream"), X_WWW_FORM_URLENCODED("x-www-form-urlencoded"),
		X_ZIP_COMPRESSED("x-zip-compressed"), JSON("json");

		private String header;

		public String getHeader() {
			return this.header;
		}

		public static ApplicationType getFromHeader(String header) {
			for (ApplicationType type : values()) {
				if (type.getHeader().equalsIgnoreCase(header)) {
					return type;
				}
			}
			return null;
		}
	}

}
