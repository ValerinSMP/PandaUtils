package darkpanda73.PandaUtils.PandaHTTPServer.Utils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpMalformedRequestException;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.FormDataMultipart;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Multipart;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart.Objects.MultipartObject;
import darkpanda73.PandaUtils.Utilities.Java.Buffer.BufferedBytesReader;
import darkpanda73.PandaUtils.Utilities.Java.Bytes.ByteUtilities;
import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public class MultipartUtils {

	public static FormDataMultipart readFormDataMultipart(String boundary, byte[] bytes)
			throws HttpMalformedRequestException {
		try {
			FormDataMultipart multipart = new FormDataMultipart(boundary);
			multipart.setObjects(readMultipartData(multipart, bytes));
			return multipart;
		} catch (HttpMalformedRequestException e) {
			throw e;
		} catch (Exception e) {
			throw new HttpMalformedRequestException();
		}
	}

	private static List<MultipartObject> readMultipartData(Multipart multipart, byte[] bytes)
			throws HttpMalformedRequestException, IOException {
		List<MultipartObject> objects = new ArrayList<>();
		MultipartObject object;

		/*
		 * Finds every section with the boundary, also searches for the last one that
		 * acts as a eof. For each section before reaching the last one, uses the
		 * readSection method.
		 */
		String boundary = "--" + multipart.getBoundary();
		int index = -1;
		int lastIndex = -1;
		int eofIndex = ByteUtilities.indexOf(bytes, (boundary + "--").getBytes());
		while ((index = ByteUtilities.indexOf(bytes, boundary.getBytes(), index + 1)) != -1) {
			lastIndex = ByteUtilities.indexOf(bytes, boundary.getBytes(), index + 1);
			object = readSection(bytes, index + boundary.getBytes().length, lastIndex);
			if (object != null) {
				objects.add(object);
			}
			if (lastIndex != eofIndex) {
				index = lastIndex - 1;
			} else {
				break;
			}
		}
		return objects;
	}

	private static MultipartObject readSection(byte[] bytes, int startIndex, int endIndex)
			throws HttpMalformedRequestException, IOException {
		// Searching for a middle new line. This middle new line is an empty line that
		// acts as a delimiter between the data and the content. The possibilities of a
		// new line should come only when there are incompatible new lines that acts
		// independently, meaning there are three possibilities
		byte newLineByte = -1;
		int emptyLineIndex = -1;
		for (byte[] array : new byte[][] {
				new byte[] { ByteUtilities.CARRIAGE_RETURN_BYTE, (newLineByte = ByteUtilities.CARRIAGE_RETURN_BYTE) },
				new byte[] { ByteUtilities.NEW_LINE_BYTE, (newLineByte = ByteUtilities.CARRIAGE_RETURN_BYTE) },
				new byte[] { ByteUtilities.NEW_LINE_BYTE, (newLineByte = ByteUtilities.NEW_LINE_BYTE) } }) {
			int indexOf = ByteUtilities.indexOf(bytes, array, startIndex);
			if (emptyLineIndex == -1 || (indexOf != -1 && indexOf < emptyLineIndex)) {
				emptyLineIndex = indexOf;
				newLineByte = array[1];
			}
		}
		if (emptyLineIndex != -1 && emptyLineIndex < endIndex) {
			MultipartObject object = new MultipartObject();
			String contentType = "text/plain";

			// Creation of the buffer to read the first half of the array that contains the
			// data about the Multipart Object
			BufferedBytesReader reader = new BufferedBytesReader(
					new ByteArrayInputStream(bytes, startIndex, (emptyLineIndex + 1) - startIndex));
			String inputToString;
			while (reader.available() != 0) {
				inputToString = new String(reader.readLine(), StandardCharsets.UTF_8);
				if (inputToString.length() != 0) {
					if (inputToString.startsWith("Content-Type: ")) {
						contentType = inputToString.substring("Content-Type: ".length());
					} else if (inputToString.startsWith("Content-Disposition: form-data; ")) {
						String[] data = inputToString.substring("Content-Disposition: form-data; ".length()).split(";");
						for (String config : data) {
							String[] splitConfig = config.split("=");
							if (splitConfig.length > 1) {
								String configTrim = splitConfig[0].trim();
								if (configTrim.equalsIgnoreCase("name")) {
									object.setName(
											StringUtilities.removeLeadingTrailingChar(splitConfig[1].trim(), '"'));
								} else if (configTrim.equalsIgnoreCase("filename")) {
									object.setFileName(
											StringUtilities.removeLeadingTrailingChar(splitConfig[1].trim(), '"'));
								}
							}
						}
					}
				}
			}
			reader.close();

			if (contentType == null) {
				throw new HttpMalformedRequestException();
			}

			// Removing leading newlines, from the start and from the end. If there aren't
			// newlines, then it uses the original indexes. The new lines being removed are
			// leftovers from the BufferedBytesReader as we try to preserve the original
			// newlines, but it also includes the ones from the format or the substrings, so
			// we try to remove this ones and keep the original ones from the content
			int startNLIndex = newLineByte == ByteUtilities.CARRIAGE_RETURN_BYTE
					? ByteUtilities.indexOf(bytes, ByteUtilities.NEW_LINE_BYTE, emptyLineIndex + 2)
					: -1;
			if (startNLIndex == emptyLineIndex + 2) {
				startNLIndex = emptyLineIndex + 3;
			}
			int endNLIndex = ByteUtilities.indexOf(bytes, ByteUtilities.CARRIAGE_RETURN_BYTE, endIndex - 2);
			if (endNLIndex == -1) {
				endNLIndex = ByteUtilities.indexOf(bytes, ByteUtilities.NEW_LINE_BYTE, endIndex - 1);
				if (endNLIndex == -1) {
					endNLIndex = endIndex;
				}
			}

			byte[] content = Arrays.copyOfRange(bytes, startNLIndex, endNLIndex);
			return object.setContent(ContentUtils.loadContent(contentType.split(";"), content));
		}
		return null;
	}
}
