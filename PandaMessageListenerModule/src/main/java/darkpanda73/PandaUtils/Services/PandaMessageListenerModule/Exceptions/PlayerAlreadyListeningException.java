package darkpanda73.PandaUtils.Services.PandaMessageListenerModule.Exceptions;

public class PlayerAlreadyListeningException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8793980530534748309L;

	public PlayerAlreadyListeningException() {
		super("This player is already being listened");
	}

}
