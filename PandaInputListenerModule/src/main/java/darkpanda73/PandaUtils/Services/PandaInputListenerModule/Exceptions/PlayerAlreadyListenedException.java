package darkpanda73.PandaUtils.Services.PandaInputListenerModule.Exceptions;

public class PlayerAlreadyListenedException extends Exception {

	private static final long serialVersionUID = 8793980530534748309L;

	public PlayerAlreadyListenedException() {
		super("This player is already being listened");
	}

}
