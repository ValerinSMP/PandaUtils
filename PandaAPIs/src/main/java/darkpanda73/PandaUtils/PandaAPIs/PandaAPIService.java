package darkpanda73.PandaUtils.PandaAPIs;

import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.LoadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.ReloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult;
import darkpanda73.PandaUtils.PandaPlugin.Exceptions.ReportResult.ReportError;
import lombok.Getter;

@PandaService(priority = -1)
public abstract class PandaAPIService<T extends PandaAbstractHook> {

	@PandaInject
	private PandaPluginClass plugin;

	private @Getter T hook;

	@LoadMethod
	protected void load() throws ReportError {
		if (this.hook == null || !this.hook.isHooked()) {
			plugin.sendInfo(getClass(), String.format("Starting hook for '%s'...", getPluginName()));

			try {
				try {
					hook = newHook();

					hook.load();

					if (!hook.isHooked()) {
						throw new Exception("Check the server logs for more information.");
					}

					plugin.sendInfo(getClass(), String.format("Hooked into '%s' successfully!", getPluginName()));
				} catch (NoClassDefFoundError e) {
					throw new Exception("Plugin is not present on the server.");
				}
			} catch (Throwable e) {
				if (isRequired()) {
					plugin.sendError(getClass(), String.format("Unable to hook into '%s'.", getPluginName()));
					throw new ReportResult.ReportError(e.getMessage(), true, e);
				} else {
					plugin.sendWarning(getClass(),
							String.format("Unable to hook into '%s'. Ignoring as it's optional.", getPluginName()));
					throw new ReportResult.ReportError(e.getMessage(), false);
				}
			}

		}
	}

	@UnloadMethod
	protected void unload() throws Throwable {
		if (this.hook != null && this.hook.isHooked()) {
			this.hook.unload();
		}
	}

	@ReloadMethod
	public void reload() throws Throwable {
		unload();
		load();
	}

	public boolean isHooked() {
		return this.hook != null && this.hook.isHooked();
	}

	protected abstract String getPluginName();

	protected abstract T newHook() throws Throwable;

	protected abstract boolean isRequired();

}
