package darkpanda73.PandaUtils.Services.PandaHTTPServerModule;

import java.net.BindException;

import org.bukkit.Bukkit;

import darkpanda73.PandaUtils.PandaColors.Messages.Objects.MessageTemplate;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;
import darkpanda73.PandaUtils.PandaHTTPServer.Utils.SettingsUtils;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaThreads.Exceptions.AlreadyStoppedException;
import darkpanda73.PandaUtils.PandaThreads.Objects.Thread.CallBack;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaIntegerField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaLongField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import lombok.Data;
import lombok.Setter;

@Data
@Setter(lombok.AccessLevel.NONE)
@PandaService(loadOn = LoadStep.LOAD, priority = 1)
public abstract class HttpServerModule implements CallBack {

	@RegisteredPandaField("config")
	public static final PandaStringField SETTINGS_HTTP_PAGEURL = new PandaStringField("Settings.HTTP.Page-url",
			"http://localhost");
	@RegisteredPandaField("config")
	public static final PandaIntegerField SETTINGS_HTTP_PORT = new PandaIntegerField("Settings.HTTP.Port", 80);
	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_HTTP_BYTESPERSECOND = new PandaLongField(
			"Settings.HTTP.Bytes-per-second", 1048576L);
	@RegisteredPandaField("config")
	public static final PandaLongField SETTINGS_HTTP_INPUT_MAXCONTENTLENGTH = new PandaLongField(
			"Settings.HTTP.Input.Max-content-length", SettingsUtils.DEFAULT_INPUT_MAX_CONTENT_LENGTH);

	@PandaInject
	private PandaPluginClass plugin;

	private HTTPServer httpServer;

	public HttpServerModule() {
		httpServer = new HTTPServer();
	}

	public String getUrl() {
		return SETTINGS_HTTP_PAGEURL.getContent();
	}

	@LoadMethod
	protected void load() throws ReportError {
		try {
			this.getHttpServer().setPort(SETTINGS_HTTP_PORT.getContent());
			this.getHttpServer().getSettings().setBytesPerSecond(SETTINGS_HTTP_BYTESPERSECOND.getContent());
			this.getHttpServer().getSettings().setMaxContentLength(SETTINGS_HTTP_INPUT_MAXCONTENTLENGTH.getContent());

			httpServer.startServer();
		} catch (BindException e) {
			throw new ReportResult.ReportError(String.format("Not able to start http server on port %d. %s",
					httpServer.getPort(), (isOptional() ? " Http server is optional, proceeding to ignore." : "")),
					!isOptional());
		} catch (Exception e) {
			throw new ReportResult.ReportError("Unexpected error.", !isOptional(), e);
		}
	}

	@UnloadMethod
	protected void unload() throws ReportError {
		if (httpServer != null) {
			try {
				httpServer.stopServer();
			} catch (AlreadyStoppedException e) {
			} catch (Exception e) {
				throw new ReportResult.ReportError("Unexpected error.", !isOptional(), e);
			}
		}
	}

	protected abstract boolean isOptional();

	@Override
	public void onError(Exception ex) {
		MessageTemplate.inst(plugin.getPrefix() + "&7<HTTP> An error has ocurred: " + ex.getMessage()).process()
				.sendMessage(Bukkit.getConsoleSender());
	}

	@Override
	public void onFinish() {
		MessageTemplate.inst(getPlugin().getPrefix() + "&7<HTTP> The HTTP server has been closed successfully")
				.process().sendMessage(Bukkit.getConsoleSender());
	}

	@Override
	public void onStart() {
		MessageTemplate.inst(getPlugin().getPrefix() + "&7<HTTP> The HTTP server will try to start").process()
				.sendMessage(Bukkit.getConsoleSender());
	}

	@Override
	public void onInterrupt() {
		MessageTemplate.inst(getPlugin().getPrefix()
				+ "&7<HTTP> The HTTP server has been interrupted. If it's not due an intended closure, is suggested to check for errors")
				.process().sendMessage(Bukkit.getConsoleSender());
	}

	@Override
	public void onInput(Object input) {
	}

}
