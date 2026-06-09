package darkpanda73.PandaUtils.PandaHTTPServer.Enums;

import java.util.Arrays;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP.ResponseStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter(lombok.AccessLevel.PUBLIC)
@AllArgsConstructor
public enum Response {
	OK(200, "200 OK"), MOVED_PERMANENTLY(301, "301 Moved Permanently"), FOUND(302, "302 Found"),
	NOT_MODIFIED(304, "304 Not Modified"), TEMPORARY_REDIRECT(307, "307 Temporary Redirect"),
	BAD_REQUEST(400, "400 Bad Request"), FORBIDDEN(403, "403 Forbidden"), NOT_FOUND(404, "404 Not Found"),
	METHOD_NOT_ALLOWED(405, "405 Method Not Allowed"), NOT_ACCEPTABLE(406, "406 Not Acceptable"),
	INTERNAL_SERVER_ERROR(500, "500 Internal Server Error"), NOT_IMPLEMENTED(501, "501 Not Implemented"),
	SERVICE_UNAVAILABLE(503, "503 Service Unavailable");

	private int code;
	private String header;

	public ResponseStatus responseStatus() {
		return new ResponseStatus(Version.V1_0, this);
	}

	public static Response getByCode(int code) {
		return Arrays.stream(values()).filter(response -> response.getCode() == code).findFirst().orElse(null);
	}
}
