package darkpanda73.PandaUtils.Utilities.Java.Buffer;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;

import darkpanda73.PandaUtils.Utilities.Java.Bytes.ByteUtilities;

public class BufferedBytesReader extends BufferedInputStream {

	private static int DEFAULT_BUFFER_SIZE = 8192;

	private boolean skipNL = false;

	public BufferedBytesReader(InputStream in) {
		super(in, DEFAULT_BUFFER_SIZE);
	}

	public BufferedBytesReader(InputStream in, int size) {
		super(in, size);
	}

	public byte[] readLine() throws IOException {
		try {
			return this.readLine(-1);
		} catch (MaxLengthAllowedReachedException e) {
		}
		return new byte[0];
	}

	public byte[] readLine(long maxLength) throws IOException, MaxLengthAllowedReachedException {
		byte[] line = new byte[this.buf.length];
		int i = 0;
		byte input = -1;
		try {
			for (; i < line.length;) {
				if (maxLength != -1 && i >= maxLength) {
					throw new MaxLengthAllowedReachedException(i, maxLength);
				}
				input = (byte) read();
				if (input == -1) {
					break;
				}
				if (input == ByteUtilities.NEW_LINE_BYTE) {
					if (i == 0 && skipNL) {
						skipNL = false;
						continue;
					} else {
						break;
					}
				} else if (input == ByteUtilities.CARRIAGE_RETURN_BYTE) {
					skipNL = true;
					break;
				} else {
					line[i++] = input;
					skipNL = false;
				}
			}
		} catch (SocketTimeoutException e) {
			e.printStackTrace();
		}
		return Arrays.copyOfRange(line, 0, i);
	}

	public String readLineAsString(Charset charset) throws IOException {
		return new String(readLine(), charset);
	}

	public String readLineAsString(Charset charset, long maxLength)
			throws IOException, MaxLengthAllowedReachedException {
		return new String(readLine(maxLength), charset);
	}

	public static class MaxLengthAllowedReachedException extends Exception {

		private static final long serialVersionUID = -3675091463518966308L;

		public MaxLengthAllowedReachedException(long length, long maxLength) {
			super(String.format("Reached max length allowed [length: %d] [allowed: %d]", length, maxLength));
		}

	}
}
