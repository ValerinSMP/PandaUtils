package darkpanda73.PandaUtils.PandaIRCClient;

public interface PandaIRCClientListener {
	public abstract void onStart(PandaIRCClient ircbot);
	public abstract void onFinish(PandaIRCClient ircbot);
	public abstract void onError(PandaIRCClient ircbot, Exception exception);
	public abstract void onConnect(PandaIRCClient ircbot);
	public abstract void onDisconnect(PandaIRCClient ircBot);
	public abstract void onInputMessage(PandaIRCClient ircbot, String input);
	public abstract void log(String log);
}
