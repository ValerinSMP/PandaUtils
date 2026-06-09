package darkpanda73.PandaUtils.Services.PandaDiscordWebhook;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject.PostInjectMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaYaml.PandaYaml;
import darkpanda73.PandaUtils.PandaYaml.Exceptions.YamlException;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageInstance;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Objects.DiscordMessageTemplate;
import darkpanda73.PandaUtils.Services.PandaDiscordWebhook.Utils.DiscordMessageUtilities;
import darkpanda73.PandaUtils.Services.PandaFilesModule.PandaFilesService;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Annotation.RegisteredPandaField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaFolder;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.PandaYamlFile;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaBooleanField;
import darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields.PandaStringField;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;

@PandaService(injectable = true, loadOn = LoadStep.ENABLE)
public class PandaDiscordService {

	private static final String NEW_LINE = System.lineSeparator();

	private static final String BOUNDARY = "----boundary-" + System.currentTimeMillis();
	private static final String BOUNDARY_DELIMITER = "--";

	private static final String RATE_LIMIT_REMAINING_START = "x-ratelimit-remaining";
	private static final String RATE_LIMIT_RESET_START = "x-ratelimit-reset";

	@RegisteredPandaField("discord")
	public static final PandaBooleanField DISCORD_ENABLE = new PandaBooleanField("Enable", false);

	@RegisteredPandaField("discord")
	public static final PandaStringField DISCORD_WEBHOOKURL = new PandaStringField("General-webhook-url", "");

	@PandaInject
	private PandaPluginClass plugin;

	@PandaInject
	private PandaFilesService filesService;

	private PandaYamlFile discordFile;

	private AsyncQueue<DiscordMessageInstance> queue;
	private List<DiscordMessageTemplate> templates;

	private boolean firstTime = true;

	public PandaDiscordService() {
		this.queue = new AsyncQueue<>();
		this.queue.setPrintErrors(true);
		this.queue.subscribe(this::sendQuery);

		this.templates = new ArrayList<>();
	}

	@PostInjectMethod
	private void postInject() {
		PandaFolder pluginFolder = filesService.getFolder("plugin");
		String path = new File(pluginFolder.getFile(), "Discord.yml").getPath();
		InputStream yamlInputStream = plugin.getResource(
				plugin.getClass().getPackage().getName().replaceAll("\\.", "/") + "/Resources/Discord.yml");

		try {
			discordFile = yamlInputStream != null ? new PandaYamlFile("discord", path, new PandaYaml(yamlInputStream))
					: new PandaYamlFile("discord", path);
		} catch (IOException | YamlException e) {
			plugin.sendError(getClass(), "Unable to use the plugin defailt Discord.yml file:");
			e.printStackTrace();
			discordFile = new PandaYamlFile("permissions", path);
		}

		filesService.addFile(discordFile);
		filesService.registerFields(getClass());

		InjectUtils.inject(DiscordMessageTemplate.class, this);
	}

	@LoadMethod
	private void load() throws ReportError {
		if (firstTime) {
			this.templates.forEach(template -> this.discordFile.registerFieldsFromObject(template));
		}

		reload();
	}

	@ReloadMethod
	private void reload() throws ReportError {
		try {
			this.updateFile();
		} catch (YamlException | IOException e) {
			throw new ReportError("Unable to update Discord file. Discord service will get disabled1:", false, e);
		}

		if (DISCORD_ENABLE.isFalse()) {
			return;
		}

		if (!isGeneralWebhookDefined()) {
			PandaPluginClass.getSimpleLogger().sendWarning(
					"No Discord general webhook has been defined, messages will require a webhook individually.");
		}
	}

	@UnloadMethod
	private void unload() {
		this.queue.stop();
		this.queue.clear();
	}

	public void registerTemplate(DiscordMessageTemplate template) {
		this.templates.add(template);
	}

	public boolean isGeneralWebhookDefined() {
		return Boolean.TRUE.equals(DISCORD_ENABLE.getContent()) && DISCORD_WEBHOOKURL.getContent() != null
				&& !DISCORD_WEBHOOKURL.getContent().isEmpty();
	}

	private void updateFile() throws FileNotFoundException, YamlException, IOException {
		PandaYaml yaml = new PandaYaml(this.discordFile.getFile());

		this.templates.forEach(template -> {
			if (!yaml.getRoot().has("Templates." + template.getId() + ".Enabled")) {
				yaml.getRoot().set("Templates." + template.getId() + ".Enabled", true);
			}
			if (!yaml.getRoot().has("Templates." + template.getId() + ".Webhook-url")) {
				yaml.getRoot().set("Templates." + template.getId() + ".Webhook-url", "");
			}

			if (!yaml.getRoot().has("Templates." + template.getId() + ".Template")) {
				DiscordMessageUtilities.templateToSection(template).toMap().forEach((key, value) -> yaml.getRoot()
						.set("Templates." + template.getId() + ".Template." + key, value));
			}

			template.setEnabled(Boolean.TRUE
					.equals(yaml.getRoot().getData("Templates." + template.getId() + ".Enabled").getBoolean()));

			template.setWebhookUrl(
					yaml.getRoot().getData("Templates." + template.getId() + ".Webhook-url").getString());
			template.setMessage(DiscordMessageUtilities
					.sectionToTemplate(yaml.getRoot().getSection("Templates." + template.getId() + ".Template")));
		});

		yaml.saveYAML(discordFile.getFile());
	}

	public void sendMessage(DiscordMessageInstance instance) {
		if (DISCORD_ENABLE.isTrue() && instance.getTemplate().isEnabled()) {
			this.queue.add(instance);
		}
	}

	private void sendQuery(DiscordMessageInstance instance) throws InterruptedException {
		if (!instance.getTemplate().isWebhookUrlDefined()) {
			return;
		}

		if (instance.getTemplate().getMessage().getContent() == null
				&& instance.getTemplate().getMessage().getEmbeds().isEmpty()) {
			throw new IllegalArgumentException("Set content or add at least one EmbedObject");
		}

		String json = instance.process();

		try {
			Integer remaining = -1;
			Long reset = -1L;

			URL url = new URL(instance.getTemplate().getWebhookUrl());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.addRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
			connection.addRequestProperty("User-Agent", "Java-DiscordWebhook-Maintained-By-DarkPanda73");
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");

			try (OutputStream outputStream = connection.getOutputStream()) {
				sendMessageToStream(instance, json, outputStream);
				outputStream.flush();

				remaining = connection.getHeaderFieldInt(connection.getHeaderFields().keySet().stream()
						.filter(key -> key != null && key.equalsIgnoreCase(RATE_LIMIT_REMAINING_START)).findFirst()
						.orElse(RATE_LIMIT_REMAINING_START), -1);
				reset = connection.getHeaderFieldLong(connection.getHeaderFields().keySet().stream()
						.filter(key -> key != null && key.equalsIgnoreCase(RATE_LIMIT_RESET_START)).findFirst()
						.orElse(RATE_LIMIT_RESET_START), -1L);
			}

			if (connection.getResponseCode() == 400) {
				PandaPluginClass.getSimpleLogger().sendError(String.format(
						"Unable to send Discord message due a bad format. Please, review the format of the message '%s', or contact with the developer and send him the next content:",
						instance.getTemplate().getId()));
				PandaPluginClass.getSimpleLogger().sendError(json);

				try (InputStream inputStream = connection.getInputStream()) {
					PandaPluginClass.getSimpleLogger().sendError(System.lineSeparator(), "Error from discord:",
							System.lineSeparator());

					try (BufferedReader reader = new BufferedReader(
							new InputStreamReader(connection.getInputStream()))) {
						PandaPluginClass.getSimpleLogger()
								.sendError(reader.lines().parallel().collect(Collectors.joining("\n")));
					}
				} catch (IOException e) {
					PandaPluginClass.getSimpleLogger().sendError(System.lineSeparator(),
							"Unable to retrieve error from discord.");
				}
			}

			connection.disconnect();

			if (remaining != -1 && remaining < 1) {
				if (reset == -1) {
					reset = System.currentTimeMillis() - 1000;
				}

				reset *= 1000;

				Thread.sleep(Math.max(reset - System.currentTimeMillis(), 1000));
			}
		} catch (InterruptedException e) {
			throw e;
		} catch (Exception e) {
			plugin.sendError(getClass(), String.format("Unable to send Discord message: %s", e.getMessage()));
			e.printStackTrace();
		}
	}

	private void sendMessageToStream(DiscordMessageInstance instance, String json, OutputStream outputStream)
			throws IOException {
		outputStream.write(BOUNDARY_DELIMITER.concat(BOUNDARY).concat(NEW_LINE).getBytes());
		outputStream.write("Content-Disposition: form-data; name=\"payload_json\"".concat(NEW_LINE).getBytes());
		outputStream.write("Content-Type: application/json".concat(NEW_LINE).concat(NEW_LINE).getBytes());

		outputStream.write(json.concat(NEW_LINE).getBytes());

		if (!instance.getAttachments().isEmpty()) {
			AtomicInteger index = new AtomicInteger();
			instance.getAttachments().forEach(attachment -> {
				try {
					outputStream.write(BOUNDARY_DELIMITER.concat(BOUNDARY).concat(NEW_LINE).getBytes());
					outputStream
							.write(String
									.format("Content-Disposition: form-data; name=\"files[%d]\"; filename=\"%s\"",
											index.getAndIncrement(), attachment.getFilename())
									.concat(NEW_LINE).getBytes());
					outputStream.write(String.format("Content-Type: %s", attachment.getContentType()).concat(NEW_LINE)
							.concat(NEW_LINE).getBytes());
					outputStream.write(attachment.getContent());
					outputStream.write(NEW_LINE.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			});
		}

		outputStream.write(BOUNDARY_DELIMITER.concat(BOUNDARY).concat(BOUNDARY_DELIMITER).concat(NEW_LINE).getBytes());
	}

}
