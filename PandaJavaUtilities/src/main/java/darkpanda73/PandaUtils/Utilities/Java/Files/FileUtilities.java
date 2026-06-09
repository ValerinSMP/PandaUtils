package darkpanda73.PandaUtils.Utilities.Java.Files;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.function.Consumer;

public class FileUtilities {

	private static final int DEFAULT_BUFFER_SIZE = 8192;

	public static void foreachFile(File file, Consumer<File> onFile) {
		if (file.isDirectory()) {
			Arrays.asList(file.listFiles()).forEach(cFile -> foreachFile(cFile, onFile));
		} else {
			onFile.accept(file);
		}
	}

	public static boolean deleteFile(File file) {
		if (file.exists() && file.isDirectory()) {
			Arrays.stream(file.listFiles()).forEach((cFile) -> deleteFile(cFile));
		}
		return file.delete();
	}

	public static void inputToFile(InputStream stream, File file) throws IOException {
		try {
			if (file.exists()) {
				file.delete();
			}

			if (!file.isDirectory()) {
				file.getParentFile().mkdirs();
				file.createNewFile();
				try (FileOutputStream outputStream = new FileOutputStream(file, false)) {
					int read;
					byte[] bytes = new byte[DEFAULT_BUFFER_SIZE];
					while ((read = stream.read(bytes)) != -1) {
						outputStream.write(bytes, 0, read);
					}
					outputStream.flush();
					outputStream.close();
					stream.close();
				}
			} else {
				file.mkdirs();
			}
		} catch (Exception e) {
			System.out.println("Failed creating file and setting content -> " + file.getPath());
			throw e;
		}
	}

	public static void copyDirectory(File source, File destination) throws IOException {
		if (source.isDirectory()) {
			privateCopyDirectory(source, destination);
		} else {
			privateCopyFile(source, destination);
		}
	}

	private static void privateCopyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
		if (!destinationDirectory.exists()) {
			destinationDirectory.mkdir();
		}
		for (String f : sourceDirectory.list()) {
			copyDirectory(new File(sourceDirectory, f), new File(destinationDirectory, f));
		}
	}

	private static void privateCopyFile(File sourceFile, File destinationFile) throws IOException {
		try (InputStream in = new FileInputStream(sourceFile);
				OutputStream out = new FileOutputStream(destinationFile)) {
			byte[] buf = new byte[1024];
			int length;
			while ((length = in.read(buf)) > 0) {
				out.write(buf, 0, length);
			}
		}
	}

}
