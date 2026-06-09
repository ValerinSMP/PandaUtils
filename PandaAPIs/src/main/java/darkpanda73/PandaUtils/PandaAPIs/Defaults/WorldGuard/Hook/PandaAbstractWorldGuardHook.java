package darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Hook;

import java.util.List;

import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Internal.AbstractInternalWorldGuard;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Internal.InternalNewWorldGuard;
import darkpanda73.PandaUtils.PandaAPIs.Defaults.WorldGuard.Objects.AbstractFlag;
import darkpanda73.PandaUtils.PandaAPIs.Objects.PandaAbstractHook;
import darkpanda73.PandaUtils.PandaPlugin.PandaPluginClass;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.Getter;

public abstract class PandaAbstractWorldGuardHook extends PandaAbstractHook {

	@PandaInject
	private static PandaPluginClass plugin;

	private @Getter AbstractInternalWorldGuard internalWorldGuard;

	@Override
	public void load() throws Throwable {
		internalWorldGuard = new InternalNewWorldGuard();

		if (internalWorldGuard != null) {
			getFlags().forEach(AbstractFlag::registerFlag);

			hooked = true;
		}
	}

	@Override
	public void unload() throws Throwable {
	}

	public PandaPluginClass getPlugin() {
		return plugin;
	}

	public void loadAPI() throws Exception {
		internalWorldGuard = new InternalNewWorldGuard();
	}

	protected abstract List<AbstractFlag<?>> getFlags();

	protected abstract void registerHandlers() throws Exception;

}
