package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.KnownHeader;
import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Method;
import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Version;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpInvalidInputStreamException;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpMalformedRequestException;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.RequestMethod;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.Settings;
import darkpanda73.PandaUtils.PandaHTTPServer.Utils.ContentUtils;
import darkpanda73.PandaUtils.Utilities.Java.Buffer.BufferedBytesReader;
import darkpanda73.PandaUtils.Utilities.Java.Buffer.BufferedBytesReader.MaxLengthAllowedReachedException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HttpInputStream {

	private @Setter(lombok.AccessLevel.NONE) Settings settings;

	public HttpInputStream(Settings settings, InputStream stream) {
		this.settings = settings;
		this.reader = new BufferedBytesReader(stream);
	}

	/*
	 * Settings
	 */

	private @Getter(lombok.AccessLevel.NONE) BufferedBytesReader reader;

	/*
	 * Request method
	 */

	private @Setter(lombok.AccessLevel.NONE) RequestMethod requestMethod;

	public void loadRequestMethod()
			throws HttpInvalidInputStreamException, HttpMalformedRequestException, MaxLengthAllowedReachedException {
		try {
			String method = new String(reader.readLine(settings.getMaxMethodLength()), StandardCharsets.UTF_8);
			if (method.matches("^[A-Z]{1,} .{1,} HTTP\\/(0\\.9|1\\.0|1\\.1)$")) {
				String[] split = method.split(" ", 3);
				Method httpMethod = Method.valueOf(split[0]);
				String[] spath = split[1].split("\\?", 2);
				String httpPath = spath[0];
				Version version = Version.valueOf("V" + split[2].split("/")[1].replaceAll("\\.", "_"));
				this.requestMethod = new RequestMethod(httpMethod, httpPath, version);
				if (spath.length > 1) {
					this.requestMethod.getParameters().putAll(
							Arrays.stream(spath[1].split("&")).map(parameter -> parameter.split("=", 2)).collect(
									Collectors.toMap(value -> value[0], value -> value.length == 2 ? value[1] : "")));
				}
			} else {
				throw new HttpMalformedRequestException();
			}
		} catch (MaxLengthAllowedReachedException e) {
			throw e;
		} catch (HttpMalformedRequestException e) {
			throw e;
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (IOException e) {
			throw new HttpInvalidInputStreamException(e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new HttpMalformedRequestException(e);
		}
	}

	/*
	 * Headers
	 */

	private HashMap<KnownHeader, String> headers = new HashMap<>();

	public void loadHeaders()
			throws HttpInvalidInputStreamException, HttpMalformedRequestException, MaxLengthAllowedReachedException {
		try {
			for (String input = reader.readLineAsString(StandardCharsets.UTF_8,
					settings.getMaxHeadersLength()); input != null
							&& input.length() != 0; input = reader.readLineAsString(StandardCharsets.UTF_8)) {
				if (input.matches("^[a-zA-Z\\-]{1,}: .*$")) {
					int index = input.indexOf(": ");
					this.headers.put(KnownHeader.getKnownHeader(input.substring(0, index)), input.substring(index + 2));
				} else {
					System.out.println("Wrong header");
					throw new HttpMalformedRequestException();
				}
			}
		} catch (MaxLengthAllowedReachedException e) {
			throw e;
		} catch (HttpMalformedRequestException e) {
			throw e;
		} catch (IOException e) {
			throw new HttpInvalidInputStreamException(e);
		} catch (Exception e) {
			throw new HttpMalformedRequestException(e);
		}
	}

	/*
	 * Content
	 */

	private Content content;

	public void loadContent()
			throws HttpInvalidInputStreamException, MaxLengthAllowedReachedException, HttpMalformedRequestException {
		this.content = ContentUtils.loadContent(this, reader);
	}

}
