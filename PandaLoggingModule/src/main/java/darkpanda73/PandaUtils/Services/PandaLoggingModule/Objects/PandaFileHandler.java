package darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import darkpanda73.PandaUtils.Services.PandaLoggingModule.Utils.LogUtilities;

public class PandaFileHandler extends FileHandler {

	private File file;

	public PandaFileHandler(File file, int limit, int count) throws IOException, SecurityException {
		super(file.getPath(), limit, count);
		this.file = file;
		this.setFormatter(new Formatter() {

			@Override
			public String format(LogRecord record) {
				return record.getMessage() + System.lineSeparator();
			}

		});
	}

	@Override
	public synchronized void setLevel(Level newLevel) throws SecurityException {
		if (newLevel == Level.OFF) {
			LogUtilities.zipLog(file);
		}

		super.setLevel(newLevel);

	}

}
