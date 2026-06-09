package darkpanda73.PandaUtils.Utilities.Java.Bytes;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class ByteUtilities {

	public static final byte CARRIAGE_RETURN_BYTE = (byte) 0x0D;
	public static final byte NEW_LINE_BYTE = (byte) 0x0A;

	public static byte[] readFileToBytes(String filePath) throws IOException {
		File file = new File(filePath);
		if (file.length() > Integer.MAX_VALUE) {
			throw new IOException("Requested too large file");
		}
		byte[] bytes = new byte[(int) file.length()];
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(file);
			fis.read(bytes);
		} finally {
			if (fis != null) {
				fis.close();
			}
		}
		return bytes;
	}

	public static int indexOf(byte[] array, byte search) {
		return indexOf(array, new byte[] { search }, 0);
	}

	public static int indexOf(byte[] array, byte search, int fromIndex) {
		return indexOf(array, new byte[] { search }, fromIndex);
	}

	public static int indexOf(byte[] array, byte[] searchArray) {
		return indexOf(array, searchArray, 0);
	}

	public static int indexOf(byte[] array, byte[] searchArray, int fromIndex) {
		for (int i = fromIndex; i < array.length - searchArray.length + 1; ++i) {
			boolean found = true;
			for (int j = 0; j < searchArray.length; ++j) {
				if (array[i + j] != searchArray[j]) {
					found = false;
					break;
				}
			}
			if (found) {
				return i;
			}
		}
		return -1;
	}

	public static int lastIndexOf(byte[] array, byte search) {
		return lastIndexOf(array, new byte[] { search }, array.length);
	}

	public static int lastIndexOf(byte[] array, byte search, int fromIndex) {
		return lastIndexOf(array, new byte[] { search }, fromIndex);
	}

	public static int lastIndexOf(byte[] array, byte[] searchArray) {
		return lastIndexOf(array, searchArray, array.length);
	}

	public static int lastIndexOf(byte[] array, byte[] searchArray, int fromIndex) {
		for (int i = fromIndex; i > -1; --i) {
			boolean found = true;
			for (int j = 0; j < searchArray.length; ++j) {
				if (i + j >= array.length || array[i + j] != searchArray[j]) {
					found = false;
					break;
				}
			}
			if (found) {
				return i;
			}
		}
		return -1;
	}
}
