package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Objects.MultipartObject;
import darkpanda73.PandaUtils.Utilities.Java.Arrays.ArrayUtilities;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@EqualsAndHashCode(callSuper = false)
public abstract class Multipart extends Content {

	private MultipartType multipartType;
	private String boundary;
	private @Setter(lombok.AccessLevel.PUBLIC) List<MultipartObject> objects = new ArrayList<>();

	public Multipart(MultipartType multipartType, String boundary) {
		super(ContentType.MULTIPART);
		this.multipartType = multipartType;
		this.boundary = boundary;
	}

	public MultipartObject getMultipartObject(String name) {
		for (MultipartObject object : this.objects) {
			if (object.getName().equals(name)) {
				return object;
			}
		}
		return null;
	}

	public Multipart asFormData() {
		return this;
	}

	public boolean isFormData() {
		return this.multipartType == MultipartType.FORM_DATA;
	}

	@Override
	public byte[] toBytes() {
		byte[] bytes = new byte[0];
		StringBuilder sb = new StringBuilder();
		for (MultipartObject object : this.objects) {
			sb.append("--" + boundary + "\r\n");
			sb.append("Content-Disposition: form-data");
			if (object.getName() != null) {
				sb.append("; name=\"" + object.getName() + "\"");
			}
			if (object.getFileName() != null) {
				sb.append("; filename=\"" + object.getFileName() + "\"");
			}
			sb.append("\r\n");
			sb.append("Content-Type: " + object.getContent().getContentTypeHeader() + "\r\n\r\n");
			bytes = ArrayUtilities.join(bytes, object.getContent().toBytes());
		}
		sb.setLength(0);
		return ArrayUtilities.join(bytes, sb.append("--" + this.boundary + "--").toString().getBytes());
	}

	@Override
	public String getContentTypeHeader() {
		return "multipart/" + multipartType.getHeader() + "; boundary=" + this.boundary;
	}

	@AllArgsConstructor
	public static enum MultipartType {
		FORM_DATA("form-data"), BYTERANGES("byteranges");

		private String header;

		public String getHeader() {
			return this.header;
		}

		public static MultipartType getFromHeader(String header) {
			for (MultipartType type : values()) {
				if (type.getHeader().equalsIgnoreCase(header)) {
					return type;
				}
			}
			return null;
		}
	}

}
