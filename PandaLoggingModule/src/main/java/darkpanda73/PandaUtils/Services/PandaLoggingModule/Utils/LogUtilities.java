package darkpanda73.PandaUtils.Services.PandaLoggingModule.Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.Utilities.Java.Compress.ZipUtilities;

public class LogUtilities {

	public static StackTraceElement getStackTrace(int offset) {
		offset += 2;
		StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
		return offset < stackTrace.length ? stackTrace[offset] : stackTrace[stackTrace.length - 1];
	}

	public static void zipLog(File logFile) {
		if (logFile != null && logFile.exists()) {
			String prefix = new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + ".";
			String suffix = ".log";
			String zipSuffix = ".log.gz";

			int number = Arrays.stream(logFile.getParentFile().listFiles()).filter(
					file -> file.isFile() && file.getName().startsWith(prefix) && file.getName().endsWith(zipSuffix))
					.map(file -> {
						try {
							return Integer.parseInt(file.getName().substring(prefix.length(),
									file.getName().length() - zipSuffix.length())) + 1;
						} catch (NumberFormatException e) {
							return null;
						}
					}).filter(Objects::nonNull).collect(Collectors.maxBy(Integer::compare)).orElse(0);

			File copyFile = new File(logFile.getParentFile(), prefix + number + suffix);
			File zipFile = new File(logFile.getParentFile(), prefix + number + zipSuffix);

			try {
				Files.copy(logFile.toPath(), copyFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

				if (zipFile.exists()) {
					zipFile.delete();
				}

				ZipUtilities.zip(new File[] { copyFile }, zipFile.getPath());

				copyFile.delete();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
