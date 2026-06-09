package darkpanda73.PandaUtils.PandaHTTPServer.Exceptions;

public abstract class HttpException extends RuntimeException {

	private static final long serialVersionUID = -6287930901018212736L;

	public HttpException() {
		super();
	}

	public HttpException(String message) {
		super(message);
	}

	public HttpException(Exception e) {
		super(e);
	}

	public HttpException(String message, Exception e) {
		super(message, e);
	}

}
