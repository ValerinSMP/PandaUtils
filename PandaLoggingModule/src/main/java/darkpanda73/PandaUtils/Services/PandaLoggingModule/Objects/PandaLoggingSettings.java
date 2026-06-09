package darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects;

import java.util.Arrays;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaFieldContainer;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringListField;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects.PandaSimpleLogger.LogRecord.LogLevel;

@RegisteredPandaFieldContainer("config")
public class PandaLoggingSettings {

	public static final PandaBooleanField LOGGING_ENABLED = new PandaBooleanField("Logging.Enabled", false);

	public static final PandaStringListField LOGGING_SHOWONCONSOLE = new PandaStringListField("Logging.Show-on-console",
			Arrays.asList(LogLevel.values()).stream().map(LogLevel::name).collect(Collectors.toList()));

	public static final PandaStringListField LOGGING_SHOWONLOGS = new PandaStringListField("Logging.Show-on-logs",
			Arrays.asList(LogLevel.values()).stream().map(LogLevel::name).collect(Collectors.toList()));

	public static final PandaStringField LOGGING_CONSOLEFORMAT = new PandaStringField("Logging.Console-format",
			"{prefix}{message}");

	public static final PandaStringField LOGGING_LOGSFORMAT = new PandaStringField("Logging.Logs-format",
			"[{level}][{class}.{method}()] {datetime} {message}");

}
