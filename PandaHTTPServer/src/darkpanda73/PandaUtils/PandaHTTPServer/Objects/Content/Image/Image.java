package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import lombok.AllArgsConstructor;

public class Image extends Content {

	private ImageType imageType;
	private byte[] image;

	public Image(ImageType imageType, byte[] image) {
		super(ContentType.IMAGE);
		this.imageType = imageType;
		this.image = image;
	}

	@Override
	public byte[] toBytes() {
		return image;
	}

	@Override
	public String getContentTypeHeader() {
		return "image/" + imageType.getHeader();
	}

	@AllArgsConstructor
	public static enum ImageType {
		APNG("apnmg"), AVIF("avif"), GIF("gif"), JPEG("jpeg"), PNG("png"), SVG_XML("svg+xml"), WEBP("webp");

		private String header;

		public String getHeader() {
			return this.header;
		}

		public static ImageType getFromHeader(String header) {
			for (ImageType type : values()) {
				if (type.getHeader().equalsIgnoreCase(header)) {
					return type;
				}
			}
			return null;
		}
	}

}
