package darkpanda73.PandaUtils.PandaAPIs.Objects;

import lombok.Getter;

public abstract class PandaAbstractHook {

	protected @Getter boolean hooked = false;

	public abstract void load() throws Throwable;

	public abstract void unload() throws Throwable;

}
