package darkpanda73.PandaUtils.PandaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaListener;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Objects.SimpleLogger;
import darkpanda73.PandaUtils.PandaPlugin.Utils.InjectUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.JavaUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.ListenerUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.ReportUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.ServiceUtils;
import darkpanda73.PandaUtils.PandaPlugin.Utils.TasksUtils;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue;
import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Getter;
import lombok.Setter;

public abstract class PandaPluginClass extends JavaPlugin {

	private static @Getter @Setter SimpleLogger simpleLogger;
	private static @Getter AsyncQueue<Runnable> asyncQueue;

	private static @Getter PandaPluginClass instance;

	private static @Getter boolean pluginLoaded;
	private static @Getter boolean pluginEnabled;
	private static @Getter boolean pluginDisabled;

	private List<Object> services = new LinkedList<>();
	private List<Listener> listeners = new LinkedList<>();
	private HashMap<Class<?>, Object> injectables = new LinkedHashMap<>();
	private boolean error = false;

	public PandaPluginClass() {
		instance = this;

		simpleLogger = new SimpleLogger(this);
		asyncQueue = new AsyncQueue<>();
		asyncQueue.subscribe((runnable) -> runnable.run());

		pluginLoaded = false;
		pluginEnabled = false;
		pluginDisabled = false;
	}

	protected void sendDebug(String... messages) {
		simpleLogger.sendDebug(null, messages);
	}

	public void sendDebug(Class<?> originClass, String... messages) {
		simpleLogger.sendDebug(originClass, messages);
	}

	protected void sendInfo(String... messages) {
		simpleLogger.sendInfo(null, messages);
	}

	public void sendInfo(Class<?> originClass, String... messages) {
		simpleLogger.sendInfo(originClass, messages);
	}

	protected void sendWarning(String... messages) {
		simpleLogger.sendWarning(null, messages);
	}

	public void sendWarning(Class<?> originClass, String... messages) {
		simpleLogger.sendWarning(originClass, messages);
	}

	protected void sendError(String... messages) {
		simpleLogger.sendError(null, messages);
	}

	public void sendError(Class<?> originClass, String... messages) {
		simpleLogger.sendError(originClass, messages);
	}

	public void onBeforeLoadPlugin() {
		sendInfo("", String.format("&ePlugin name: &7%s", this.getName()),
				String.format("&eVersion: &7%s", this.getDescription().getVersion()),
				String.format("&eAuthor/s: &7%s",
						this.getDescription().getAuthors().stream().collect(Collectors.joining(", "))),
				"", "&eStarting loading process...", "");
	}

	public void onAfterLoadPlugin() {
		pluginLoaded = true;
	}

	public void onBeforeEnablePlugin() {
	}

	public void onAfterEnablePlugin() {
		pluginEnabled = true;
		sendInfo("", "&ePlugin loaded successfully!", "");
	}

	public void onBeforeReloadPlugin() {
	}

	public void onAfterReloadPlugin() {
		sendInfo("", "&ePlugin reloaded successfully!", "");
	}

	public void onBeforeDisablePlugin() {
		pluginDisabled = true;
	}

	public void onAfterDisablePlugin() {
		sendInfo("", "&ePlugin unloaded successfully!", "");
	}

	@Override
	public final void onLoad() {
		// Starts the initial data for the plugin
		this.initData();

		this.privateLoad();
	}

	@Override
	public final void onEnable() {
		this.privateEnable();
	}

	@Override
	public final void onDisable() {
		this.privateDisable();
	}

	public final void reloadPlugin() {
		this.privateReload();
	}

	public abstract String getPrefix();

	private void privateLoad() {
		if (error) {
			throw new RuntimeException("Unable to load plugin");
		}

		ReportResult result = ReportUtils.generateResult((reportResult) -> {
			// Starts the load method
			onBeforeLoadPlugin();

			// Executes the LoadMethod on every service which is set to load on the load
			// step
			services.forEach(service -> {
				try {
					PandaService pandaService = service.getClass().getAnnotation(PandaService.class);

					if (pandaService.loadOn() == LoadStep.LOAD && !ServiceUtils.loadService(this, service)) {
						throw new RuntimeException(
								String.format("Unable to load service '%s'", service.getClass().getSimpleName()));
					}
				} catch (Exception e) {
					throw new RuntimeException(
							String.format("Unable to load service '%s'", service.getClass().getSimpleName()), e);
				}
			});

			// Ends the enable method
			onAfterLoadPlugin();
		});

		checkResult(result);
	}

	private void privateEnable() {
		if (error) {
			Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().disablePlugin(this));
			throw new RuntimeException("Unable to enable plugin");
		}

		ReportResult result = ReportUtils.generateResult((reportResult) -> {
			// Starts the enable method
			onBeforeEnablePlugin();

			// Executes the LoadMethod on every service which is set to load on the enable
			// step
			services.forEach(service -> {
				try {
					PandaService pandaService = service.getClass().getAnnotation(PandaService.class);

					if (pandaService.loadOn() == LoadStep.ENABLE && !ServiceUtils.loadService(this, service)) {
						throw new RuntimeException(
								String.format("Unable to load service '%s'", service.getClass().getSimpleName()));
					}
				} catch (Exception e) {
					throw new RuntimeException(
							String.format("Unable to load service '%s'", service.getClass().getSimpleName()), e);
				}
			});

			// Registers all the listeners registered on the plugin.
			listeners.forEach(listener -> Bukkit.getPluginManager().registerEvents(listener, this));

			// Ends the enable method
			onAfterEnablePlugin();
		});

		checkResult(result);
	}

	private void privateDisable() {
		if (error) {
			throw new RuntimeException("Unable to disable plugin");
		}

		ReportResult result = ReportUtils.generateResult((reportResult) -> {
			// Starts the enable method
			onBeforeDisablePlugin();

			// Executes the LoadMethod on every service which is set to load on the enable
			// step. This step only disables if it's a spigot call, meaning that it's being
			// execute by the onDisable method, meaning a staff member requested the
			// shutdown of the plugin
			services.stream().sorted((s1, s2) -> {
				PandaService serviceInfo1 = s1.getClass().getAnnotation(PandaService.class);
				PandaService serviceInfo2 = s2.getClass().getAnnotation(PandaService.class);
				int priority1 = serviceInfo1 != null ? serviceInfo1.priority() : 999;
				int priority2 = serviceInfo2 != null ? serviceInfo2.priority() : 999;

				return priority2 - priority1;
			}).forEach(service -> {
				try {
					ServiceUtils.unloadService(this, service);
				} catch (Exception e) {
					reportResult.addErrors(new ReportResult.ReportError(String.format(
							"Unable to disable service '%s' properly", service.getClass().getSimpleName()), false, e));
				}
			});

			// Ends the enable method
			onAfterDisablePlugin();
		});

		checkResult(result);
	}

	private void privateReload() {
		if (error) {
			throw new RuntimeException("Unable to reload plugin");
		}

		ReportResult result = ReportUtils.generateResult((reportResult) -> {
			// Starts the load method
			onBeforeReloadPlugin();

			// Executes the LoadMethod on every service which is set to load on the load
			// step
			services.forEach(service -> {
				try {
					if (!ServiceUtils.reloadService(this, service)) {
						throw new RuntimeException(
								String.format("Unable to reload service '%s'", service.getClass().getSimpleName()));
					}
				} catch (Exception e) {
					throw new RuntimeException(
							String.format("Unable to reload service '%s'", service.getClass().getSimpleName()), e);
				}
			});

			// Ends the enable method
			onAfterReloadPlugin();
		});

		checkResult(result);

	}

	private void checkResult(ReportResult result) {
		if (result.getErrors().size() > 0) {
			if (result.isCritical()) {
				sendError("Critical error has occurred:");
				result.getErrors().forEach(error -> {
					sendError(error.getMessage());
					if (error.getCause() != null) {
						error.getCause().printStackTrace();
					}
				});

				if (isEnabled()) {
					Bukkit.getScheduler().runTask(this, () -> Bukkit.getPluginManager().disablePlugin(this));
				}
				this.error = true;
				throw new RuntimeException("Unable to enable plugin");
			} else {
				sendWarning("Some non-critical errors have been retrieved during the process:");
				result.getErrors().forEach(error -> {
					sendWarning(error.getMessage());
					if (error.getCause() != null) {
						error.getCause().printStackTrace();
					}
				});
			}
		}
	}

	private void initData() {
		try {
			sendInfo(String.format("&ePreparing all data for '%s'. Loading plugin data...", this.getName()));

			InjectUtils.inject(JavaUtils.class, this);
			InjectUtils.inject(TasksUtils.class, this);

			// Puts the plugin as an injectable class in the map.
			this.injectables.put(this.getClass(), this);

			List<Pair<Integer, Object>> currentInjectablesFound = new ArrayList<>();

			// Finds every class which contains the annotation @PandaService to start
			// registering
			ServiceUtils.getServiceConstructors(this, this.getClassLoader()).forEach(constructor -> {
				try {
					Object service = constructor.newInstance();

					JavaUtils.callMethodIfAnnotationPresent(service.getClass(), service,
							PandaService.PostConstructMethod.class);

					services.add(service);

					PandaService pandaService = service.getClass().getAnnotation(PandaService.class);

					if (pandaService.injectable()) {
						currentInjectablesFound.add(Pair.of(pandaService.priority(), service));
					}
				} catch (Throwable e) {
					System.out.println(String.format("Unable to retrieve constructor for service '%s': %s",
							constructor.getDeclaringClass().getSimpleName(), e.getMessage()));
					e.printStackTrace();
				}
			});

			currentInjectablesFound.sort((pair1, pair2) -> Integer.compare(pair1.getFirst(), pair2.getFirst()));
			currentInjectablesFound
					.forEach(injectable -> injectables.put(injectable.getSecond().getClass(), injectable.getSecond()));

			// Sorts the services list to be sorted by their priorities
			services.sort((s1, s2) -> {
				PandaService serviceInfo1 = s1.getClass().getAnnotation(PandaService.class);
				PandaService serviceInfo2 = s2.getClass().getAnnotation(PandaService.class);
				int priority1 = serviceInfo1 != null ? serviceInfo1.priority() : 999;
				int priority2 = serviceInfo2 != null ? serviceInfo2.priority() : 999;

				return priority1 - priority2;
			});

			sendInfo(String.format("&eRegistered services: %s", services.stream()
					.map(service -> service.getClass().getSimpleName()).collect(Collectors.joining(", ", "[", "]"))));

			// Finds every class which contains the annotation @PandaListener to start
			// registering
			ListenerUtils.getListenerConstructors(this, this.getClassLoader()).forEach(constructor -> {
				try {
					Listener listener = constructor.newInstance();

					JavaUtils.callMethodIfAnnotationPresent(listener.getClass(), listener,
							PandaListener.PostConstructMethod.class);

					listeners.add(listener);

					PandaListener pandaListener = listener.getClass().getAnnotation(PandaListener.class);

					if (pandaListener.injectable()) {
						injectables.put(constructor.getDeclaringClass(), listener);
					}
				} catch (Throwable e) {
					if (!constructor.getDeclaringClass().getAnnotation(PandaListener.class).optional()) {
						System.out.println(String.format("Unable to retrieve constructor for listener '%s': %s",
								constructor.getDeclaringClass().getSimpleName(), e.getMessage()));
						e.printStackTrace();
					}
				}
			});

			sendInfo(String.format("&eRegistered listeners: %s", listeners.stream()
					.map(listener -> listener.getClass().getSimpleName()).collect(Collectors.joining(", ", "[", "]"))));

			// If the list of injectables is not empty, it'll start injecting the objects on
			// every class which has any field with the @PandaInject annotation.
			if (!this.injectables.isEmpty()) {
				sendInfo(String.format("&eRegistered injectables: %s.", injectables.keySet().stream()
						.map(injectable -> injectable.getSimpleName()).collect(Collectors.joining(", ", "[", "]"))));

				services.stream().forEach(service -> InjectUtils.inject(service, injectables));
				listeners.stream().forEach(listener -> InjectUtils.inject(listener, injectables));

				JavaUtils.getClasses().forEach(clazz -> InjectUtils.inject(clazz, injectables));
			}

			sendInfo("&eLoaded plugin data successfully.");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public ClassLoader getPluginClassLoader() {
		return super.getClassLoader();
	}

	@SuppressWarnings("unchecked")
	public <T> T getInjectable(Class<T> clazz) {
		return (T) this.injectables.get(clazz);
	}

}
