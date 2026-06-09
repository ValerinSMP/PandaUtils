package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.KnownHeader;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application.OctetStreamApplication;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.ResponseStatus;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HttpOutputStream {

	private static final long EIGHT_PART_SECOND = 125L;

	/*
	 * Response
	 */
	private ResponseStatus responseStatus;
	private Content content;
	private HashMap<KnownHeader, String> headers = new HashMap<>();
	private byte[] contentBytes;

	private OutputStatus status = OutputStatus.REQUIRING_RESPONSE_STATUS;
	private @Getter(lombok.AccessLevel.NONE) OutputStream stream;

	public HttpOutputStream(OutputStream stream) {
		this.stream = stream;
	}

	/*
	 * Initializers
	 */

	public HttpOutputStream loadValues() {
		StringBuilder sb = new StringBuilder();
		this.headers.putIfAbsent(KnownHeader.CONNECTION, "close");
		this.headers.putIfAbsent(KnownHeader.CACHE_CONTROL, "no-store");
		if (content != null) {
			contentBytes = this.content.toBytes();
			headers.put(KnownHeader.CONTENT_TYPE, this.content.getContentTypeHeader());
			headers.put(KnownHeader.CONTENT_LENGTH, String.valueOf(this.contentBytes.length));

			if (this.content.isApplication() && this.content.asApplication().isOctetStream()) {
				OctetStreamApplication app = this.content.asApplication().asOctetStream();
				sb.append(app.getDisposition().getHeader());
				if (app.getName() != null) {
					sb.append("; name=\"" + app.getName() + "\"");
				}
				if (app.getFileName() != null) {
					sb.append("; filename=\"" + app.getFileName() + "\"");
				}
				headers.put(KnownHeader.CONTENT_DISPOSITION, sb.toString());
				sb.setLength(0);
			}
		} else {
			headers.put(KnownHeader.CONTENT_LENGTH, String.valueOf(0));
			contentBytes = null;
		}
		return this;
	}

	/*
	 * Senders
	 */

	public void sendData(long bytesPerSecond) throws Exception {
		sendResponseStatus();
		sendHeaders();
		sendContent(bytesPerSecond);
	}

	public void sendResponseStatus() throws Exception {
		if (status == OutputStatus.REQUIRING_RESPONSE_STATUS) {
			if (this.responseStatus == null) {
				new Exception("Response Status is null");
			}
			this.stream.write(("HTTP/" + responseStatus.getVersion().name().substring(1).replaceAll("_", ".") + " "
					+ responseStatus.getResponse().getHeader() + "\r\n").getBytes());
			this.stream.flush();
			status = OutputStatus.REQUIRING_HEADERS;
		} else {
			throw new Exception("Sending response status on wrong status");
		}
	}

	public void sendHeaders() throws Exception {
		if (status == OutputStatus.REQUIRING_HEADERS) {
			if (headers == null) {
				loadValues();
			}
			headers.forEach((header, value) -> {
				try {
					this.stream.write((header.getHeaderKey() + ": " + value + "\r\n").getBytes());
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			});
			this.stream.write(("\r\n").getBytes());
			this.stream.flush();
			status = OutputStatus.REQUIRING_CONTENT;
		} else {
			throw new Exception("Sending headers on wrong status");
		}
	}

	public void sendContent(long bytesPerSecond) throws Exception {
		if (status == OutputStatus.REQUIRING_CONTENT) {
			if (this.contentBytes != null) {
				if (bytesPerSecond != -1) {
					ByteArrayInputStream byteBuffer = new ByteArrayInputStream(this.contentBytes);

					long size = bytesPerSecond / 8;
					if (size > Integer.MAX_VALUE) {
						size = Integer.MAX_VALUE;
					}

					long lastTime;
					int bytesToSend;
					byte[] data = new byte[(int) size];

					while ((bytesToSend = byteBuffer.read(data)) != -1) {
						lastTime = System.currentTimeMillis();
						this.stream.write(data, 0, bytesToSend);
						this.stream.flush();

						long timeToSleep = EIGHT_PART_SECOND - (System.currentTimeMillis() - lastTime);
						if (timeToSleep >= 0) {
							try {
								Thread.sleep(timeToSleep);
							} catch (Exception e) {
								continue;
							}
						}
					}
				} else {
					this.stream.write(this.contentBytes);
					this.stream.flush();
				}
			}
			this.stream.close();
			status = OutputStatus.COMPLETED;
		} else {
			throw new Exception("Sending content on wrong status");
		}
	}

	/*
	 * Enums
	 */

	public enum OutputStatus {
		REQUIRING_RESPONSE_STATUS, REQUIRING_HEADERS, REQUIRING_CONTENT, COMPLETED;
	}

}
