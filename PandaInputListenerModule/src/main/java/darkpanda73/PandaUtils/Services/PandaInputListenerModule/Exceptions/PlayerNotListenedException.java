package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Exceptions;

public class PlayerNotListenedException extends Exception {

	private static final long serialVersionUID = 2280390085918915521L;

	public PlayerNotListenedException() {
		super("This player is not being listened");
	}

}
