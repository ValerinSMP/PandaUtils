package darkpanda73.PandaUtils.PandaHTTPServer.Utils;

import java.io.IOException;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.KnownHeader;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpInvalidInputStreamException;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpMalformedRequestException;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content.ContentType;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Empty;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.Application.ApplicationType;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.JsonApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.OctetStreamApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.PdfApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.XZipCompressedApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.XwwwFormUrlEncodedApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.ApngImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.AvifImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.GifImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.Image.ImageType;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.JpegImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.PngImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.SvgXmlImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Image.WebpImage;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Multipart.MultipartType;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.CssText;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.HtmlText;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.JavascriptText;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.PlainText;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Text.Text.TextType;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.Utilities.Java.Buffer.BufferedBytesReader;
import darkpanda73.PandaUtils.Utilities.Java.Buffer.BufferedBytesReader.MaxLengthAllowedReachedException;

public class ContentUtils {

	/**
	 * BufferedBytesReader provider must be offered while pointing to the start of
	 * the content.
	 */
	public static Content loadContent(HttpInputStream input, BufferedBytesReader reader)
			throws HttpInvalidInputStreamException, MaxLengthAllowedReachedException, HttpMalformedRequestException {
		Content content = new Empty();
		try {
			int length = Integer.parseInt(input.getHeaders().getOrDefault(KnownHeader.CONTENT_LENGTH, "0"));
			if (length > input.getSettings().getMaxContentLength()) {
				throw new MaxLengthAllowedReachedException(length, input.getSettings().getMaxContentLength());
			}
			if (length != 0) {
				byte first = (byte) reader.read();
				byte[] bytes = new byte[length];
				if (first != 0x0A) {
					bytes[0] = first;
					length--;
				}
				int read = 0;
				try {
					while (read < length) {
						read += reader.read(bytes, bytes.length - length + read, length - read);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (read == length) {
					return loadContent(input.getHeaders().getOrDefault(KnownHeader.CONTENT_TYPE, "").split(";"), bytes);
				} else {
					throw new HttpMalformedRequestException();
				}
			}
		} catch (MaxLengthAllowedReachedException e) {
			throw e;
		} catch (HttpMalformedRequestException e) {
			throw e;
		} catch (IOException e) {
			throw new HttpInvalidInputStreamException();
		} catch (Exception e) {
			throw new HttpMalformedRequestException();
		}
		return content;
	}

	/**
	 * The contentTypeData must follow the structure on the Content-Type:
	 * 
	 * Content-Type: type;config1=value1;config2=value2...
	 * 
	 * The bytes field must start from the very beginning of the content data.
	 */
	public static Content loadContent(String[] contentTypeData, byte[] bytes) throws HttpMalformedRequestException {
		try {
			if (contentTypeData.length == 0 || contentTypeData[0] == null) {
				return new OctetStreamApplication(bytes);
			}

			String[] contentType = contentTypeData[0].split("/", 2);
			switch (ContentType.valueOf(contentType[0].toUpperCase())) {
			case MULTIPART:
				switch (MultipartType.getFromHeader(contentType[1])) {
				case FORM_DATA:
					return MultipartUtils.readFormDataMultipart(contentTypeData[1].split("=")[1], bytes);
				default:
					throw new HttpMalformedRequestException();
				}
			case TEXT:
				switch (TextType.getFromHeader(contentType[1])) {
				case PLAIN:
					return new PlainText(new String(bytes));
				case HTML:
					return new HtmlText(new String(bytes));
				case CSS:
					return new CssText(new String(bytes));
				case JAVASCRIPT:
					return new JavascriptText(new String(bytes));
				default:
					throw new HttpMalformedRequestException();
				}
			case APPLICATION:
				switch (ApplicationType.getFromHeader(contentType[1])) {
				case OCTET_STREAM:
					return new OctetStreamApplication(bytes);
				case PDF:
					return new PdfApplication(bytes);
				case X_WWW_FORM_URLENCODED:
					return new XwwwFormUrlEncodedApplication(bytes);
				case X_ZIP_COMPRESSED:
					return new XZipCompressedApplication(bytes);
				case JSON:
					return new JsonApplication(bytes);
				default:
					throw new HttpMalformedRequestException();
				}
			case IMAGE:
				switch (ImageType.getFromHeader(contentType[1])) {
				case APNG:
					return new ApngImage(bytes);
				case AVIF:
					return new AvifImage(bytes);
				case GIF:
					return new GifImage(bytes);
				case JPEG:
					return new JpegImage(bytes);
				case PNG:
					return new PngImage(bytes);
				case SVG_XML:
					return new SvgXmlImage(bytes);
				case WEBP:
					return new WebpImage(bytes);
				default:
					throw new HttpMalformedRequestException();
				}
			default:
				throw new HttpMalformedRequestException();
			}
		} catch (HttpMalformedRequestException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpMalformedRequestException();
		}
	}

}
