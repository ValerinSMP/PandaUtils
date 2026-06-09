package darkpanda73.PandaUtils.PandaHTTPServer.Exceptions;

public class HttpInvalidInputStreamException extends HttpException {

	private static final long serialVersionUID = 1479215410791925117L;

	public HttpInvalidInputStreamException(Exception e) {
		super("There's no valid input stream available to load data", e);
	}

	public HttpInvalidInputStreamException() {
		this(null);
	}

}