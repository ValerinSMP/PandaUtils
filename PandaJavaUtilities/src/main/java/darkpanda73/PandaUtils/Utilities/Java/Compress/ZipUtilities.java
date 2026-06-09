package darkpanda73.PandaUtils.Utilities.Java.Compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtilities {

	private static final int BUFFER_SIZE = 4096;

	public static void zip(List<File> files, String destZipFile) throws FileNotFoundException, IOException {
		zip(files.toArray(new File[files.size()]), destZipFile);
	}

	public static void zip(File[] files, String destZipFile) throws FileNotFoundException, IOException {
		ZipOutputStream zos = zipOutputStream(files, destZipFile);
		zos.flush();
		zos.close();
	}

	public static ZipOutputStream zipOutputStream(File[] files, String destZipFile)
			throws FileNotFoundException, IOException {
		return zipOutputStream(files, new FileOutputStream(destZipFile));
	}

	public static ZipOutputStream zipOutputStream(File[] files, OutputStream output)
			throws FileNotFoundException, IOException {
		ZipOutputStream zos = new ZipOutputStream(output);
		zos.setLevel(Deflater.BEST_COMPRESSION);
		for (File file : files) {
			if (file.isDirectory()) {
				zipDirectory(file, file.getName(), zos);
				continue;
			}
			zipFile(file, zos);
		}
		return zos;
	}

	public static void zipDirectory(File folder, String parentFolder, ZipOutputStream zos)
			throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(file, String.valueOf(parentFolder) + "/" + file.getName(), zos);
			} else {
				zipFile(new FileInputStream(file), zos, String.valueOf(parentFolder), file.getName());
			}
		}
	}

	public static void zipFile(File file, ZipOutputStream zos) throws FileNotFoundException, IOException {
		zipFile(new FileInputStream(file), zos, null, file.getName());
	}

	public static void zipFile(InputStream input, ZipOutputStream zos, String directoryName, String fileName)
			throws FileNotFoundException, IOException {
		zos.putNextEntry(new ZipEntry((directoryName != null ? directoryName.concat("/") : "").concat(fileName)));
		BufferedInputStream bis = new BufferedInputStream(input);
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = bis.read(bytesIn)) != -1) {
			zos.write(bytesIn, 0, read);
		}
		bis.close();
		zos.closeEntry();
	}

	public static void unzipFileSameDirectory(File file) throws IOException {
		byte[] buffer = new byte[1024];

		try (ZipInputStream zis = new ZipInputStream(new FileInputStream(file))) {
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				File newFile = new File(file.getParentFile(), zipEntry.getName());

				String destDirPath = file.getParentFile().getCanonicalPath();
				String destFilePath = newFile.getCanonicalPath();

				if (!destFilePath.startsWith(destDirPath + File.separator)) {
					throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
				}

				if (zipEntry.isDirectory()) {
					if (!newFile.isDirectory() && !newFile.mkdirs()) {
						throw new IOException("Failed to create directory " + newFile);
					}
				} else {
					File parent = newFile.getParentFile();
					if (!parent.isDirectory() && !parent.mkdirs()) {
						throw new IOException("Failed to create directory " + parent);
					}

					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();
				}
				zipEntry = zis.getNextEntry();
			}

			zis.closeEntry();
		}
	}
}