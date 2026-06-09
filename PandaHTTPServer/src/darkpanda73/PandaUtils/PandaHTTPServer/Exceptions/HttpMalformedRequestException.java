package darkpanda73.PandaUtils.PandaHTTPServer.Exceptions;

public class HttpMalformedRequestException extends HttpException {

	private static final long serialVersionUID = 9140082989373075788L;

	public HttpMalformedRequestException(Exception e) {
		super("Received HTTP request contains malformed data", e);
	}

	public HttpMalformedRequestException() {
		this((Exception) null);
	}

	public HttpMalformedRequestException(String http) {
		super(String.format("Received HTTP request contains malformed data: %s", http));
	}

}