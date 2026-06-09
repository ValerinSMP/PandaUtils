package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

import lombok.AllArgsConstructor;

public class HtmlText extends Text {

	private HtmlCharset charset;

	public HtmlText(byte[] content) {
		this(content, HtmlCharset.UTF_8);
	}

	public HtmlText(String content) {
		this(content, HtmlCharset.UTF_8);
	}

	public HtmlText(byte[] content, HtmlCharset charset) {
		this(new String(content, charset.getJavaCharset()));
	}

	public HtmlText(String content, HtmlCharset charset) {
		super(TextType.HTML, content);
		this.charset = charset;
	}

	@Override
	public String getContentTypeHeader() {
		return super.getContentTypeHeader() + "; " + charset.getHeader();
	}

	@AllArgsConstructor
	public static enum HtmlCharset {
		UTF_8("charset=UTF-8", StandardCharsets.UTF_8), UTF_16("charset=UTF-16", StandardCharsets.UTF_16),
		ASCII("charset=US-ASCII", StandardCharsets.US_ASCII),
		ISO_8859_1("charset=ISO-8859-1", StandardCharsets.ISO_8859_1);

		private String header;
		private Charset javaCharset;

		public String getHeader() {
			return this.header;
		}

		public Charset getJavaCharset() {
			return this.javaCharset;
		}
	}

}
