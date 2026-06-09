package darkpanda73.PandaUtils.Services.PandaLoggingModule.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.logging.Level;
import java.util.logging.Logger;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects.PandaFileHandler;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects.PandaLoggingSettings;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects.PandaSimpleLogger;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Utils.LogUtilities;
import lombok.Getter;

@PandaService(priority = -997, loadOn = LoadStep.LOAD)
public class PandaLoggingService {

	private static @Getter PandaSimpleLogger simpleLogger;

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private PandaFolder logsFolder;
	private Logger logger;

	@PostInjectMethod
	private void postInject() throws SecurityException, IOException {
		this.filesService.addFolder(logsFolder = new PandaFolder("logs",
				this.filesService.getFolder("plugin").getFile().getPath() + "/Logs"));

		this.filesService.registerFields(PandaLoggingSettings.class);

		File logFile = new File(logsFolder.getFile(), "latest.log");

		if (logFile.exists()) {
			if (Files.size(logFile.toPath()) > 0L) {
				LogUtilities.zipLog(logFile);
			}
			logFile.delete();
		}

		logsFolder.create();
		logFile.createNewFile();

		this.logger = Logger.getLogger(this.getClass().getName());
		this.logger.setUseParentHandlers(false);
		this.logger.setLevel(Level.ALL);
		this.logger.addHandler(new PandaFileHandler(logFile, 1024000, 1));

		simpleLogger = new PandaSimpleLogger(this.plugin, this.logger);
		PandaPluginClass.setSimpleLogger(simpleLogger);
	}

}