package darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaColors.Messages.Objects.Replacement;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Objects.SimpleLogger;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Objects.PandaSimpleLogger.LogRecord.LogLevel;
import darkpanda73.PandaUtils.Services.PandaLoggingModule.Utils.LogUtilities;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

public class PandaSimpleLogger extends SimpleLogger {

	private Logger logger;
	private AsyncQueue<LogRecord> queue;

	public PandaSimpleLogger(PandaPluginClass plugin, Logger logger) {
		super(plugin);
		this.logger = logger;
		this.queue = new AsyncQueue<>();
		this.queue.subscribe((logRecord) -> {
			this.logger.log(Level.ALL, MessageTemplate.inst(PandaLoggingSettings.LOGGING_LOGSFORMAT.getContent())
					.setColor(false).setReplacements(logRecord.getReplacements()).toString());
		});
	}

	@Override
	public void sendDebug(Class<?> originClass, String... messages) {
		if (PandaLoggingSettings.LOGGING_ENABLED.isTrue()) {
			StackTraceElement traceExecutor = LogUtilities.getStackTrace(2);
			Arrays.stream(messages).forEach(message -> sendLog(LogLevel.DEBUG, traceExecutor, message));
		} else {
			super.sendInfo(originClass, messages);
		}
	}

	@Override
	public void sendInfo(Class<?> originClass, String... messages) {
		if (PandaLoggingSettings.LOGGING_ENABLED.isTrue()) {
			StackTraceElement traceExecutor = LogUtilities.getStackTrace(2);
			Arrays.stream(messages).forEach(message -> sendLog(LogLevel.INFO, traceExecutor, message));
		} else {
			super.sendInfo(originClass, messages);
		}
	}

	@Override
	public void sendWarning(Class<?> originClass, String... messages) {
		if (PandaLoggingSettings.LOGGING_ENABLED.isTrue()) {
			StackTraceElement traceExecutor = LogUtilities.getStackTrace(2);
			Arrays.stream(messages).forEach(message -> sendLog(LogLevel.WARNING, traceExecutor, message));
		} else {
			super.sendWarning(originClass, messages);
		}
	}

	@Override
	public void sendError(Class<?> originClass, String... messages) {
		if (PandaLoggingSettings.LOGGING_ENABLED.isTrue()) {
			StackTraceElement traceExecutor = LogUtilities.getStackTrace(2);
			Arrays.stream(messages).forEach(message -> sendLog(LogLevel.ERROR, traceExecutor, message));
		} else {
			super.sendError(originClass, messages);
		}
	}

	public void sendLog(LogLevel logLevel, String message) {
		sendLog(logLevel, LogUtilities.getStackTrace(1), message);
	}

	private void sendLog(LogLevel logLevel, StackTraceElement executorTrace, String message) {
		boolean consoleEnabled = PandaLoggingSettings.LOGGING_SHOWONCONSOLE.getContent().stream()
				.anyMatch(value -> value.equalsIgnoreCase(logLevel.name()));
		boolean logEnabled = PandaLoggingSettings.LOGGING_SHOWONLOGS.getContent().stream()
				.anyMatch(value -> value.equalsIgnoreCase(logLevel.name()));

		if (consoleEnabled || logEnabled) {
			Replacement[] replacements = new Replacement[] { new Replacement("{prefix}", () -> getPlugin().getPrefix()),
					new Replacement("{level}", () -> logLevel.name()),
					new Replacement("{class}", () -> executorTrace.getClassName()),
					new Replacement("{method}", () -> executorTrace.getMethodName()),
					new Replacement("{datetime}",
							() -> new SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
									.format(new Date(System.currentTimeMillis()))),
					new Replacement("{message}", () -> message), };

			if (consoleEnabled) {
				MessageTemplate.inst(PandaLoggingSettings.LOGGING_CONSOLEFORMAT.getContent())
						.setReplacements(replacements).process().sendMessage(Bukkit.getConsoleSender());
			}

			if (logEnabled) {
				queue.add(new LogRecord(logLevel, replacements, message));
			}
		}
	}

	@RequiredArgsConstructor
	@Getter
	public static class LogRecord {

		public static enum LogLevel {
			DEBUG, INFO, WARNING, ERROR, NONE
		}

		private @NonNull LogLevel level;
		private @NonNull Replacement[] replacements;
		private @NonNull String message;
		private long timeStamp = System.currentTimeMillis();

	}

}
