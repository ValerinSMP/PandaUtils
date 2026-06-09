package darkpanda73.PandaUtils.PandaThreads.Exceptions;

public class AlreadyStoppedException extends Exception {
	
	private static final long serialVersionUID = 539782149378597200L;

	@Override
	public String getMessage() {
		return "This thread is already stopped!";
	}
}
