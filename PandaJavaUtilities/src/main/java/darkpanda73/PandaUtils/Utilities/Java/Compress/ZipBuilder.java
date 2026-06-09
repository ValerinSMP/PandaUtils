package darkpanda73.PandaUtils.Utilities.Java.Compress;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class ZipBuilder {

	private static final int BUFFER_SIZE = 4096;

	private @NonNull File destinationFile;
	private @Setter Predicate<File> blacklistedPredicate;
	private List<File> includedFiles = new ArrayList<>();

	public void addFile(@NonNull File file) {
		this.includedFiles.add(file);
	}

	public void addFiles(@NonNull File... files) {
		for (File file : files) {
			this.includedFiles.add(file);
		}
	}

	public void build() throws FileNotFoundException, IOException {
		try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destinationFile))) {
			for (File file : includedFiles) {
				if (file.isDirectory()) {
					zipDirectory(zos, file, file.getName());
					continue;
				}
				zipFile(zos, file);
			}

			zos.flush();
			zos.close();
		}
	}

	private void zipDirectory(ZipOutputStream zos, File folder, String parentFolder)
			throws FileNotFoundException, IOException {
		for (File file : folder.listFiles()) {
			if (file.isDirectory()) {
				zipDirectory(zos, file, parentFolder + "/" + file.getName());
			} else {
				zipFile(zos, parentFolder, file);
			}
		}
	}

	private void zipFile(ZipOutputStream zos, File file) throws FileNotFoundException, IOException {
		zipFile(zos, null, file);
	}

	private void zipFile(ZipOutputStream zos, String directory, File file) throws FileNotFoundException, IOException {
		if (this.blacklistedPredicate != null && this.blacklistedPredicate.test(file)) {
			return;
		}

		zos.putNextEntry(new ZipEntry((directory != null ? directory.concat("/") : "").concat(file.getName())));
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
		byte[] bytesIn = new byte[BUFFER_SIZE];
		int read = 0;
		while ((read = bis.read(bytesIn)) != -1) {
			zos.write(bytesIn, 0, read);
		}
		bis.close();
		zos.closeEntry();
	}

}