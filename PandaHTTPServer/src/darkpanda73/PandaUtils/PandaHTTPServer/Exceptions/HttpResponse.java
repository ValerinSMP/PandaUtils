package darkpanda73.PandaUtils.PandaHTTPServer.Exceptions;

import java.util.HashMap;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.KnownHeader;
import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Response;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Content;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
public class HttpResponse extends HttpException {

	private static final long serialVersionUID = -4271375595005765725L;

	private @Setter(lombok.AccessLevel.NONE) @NonNull Response response;
	private @Setter(lombok.AccessLevel.NONE) Content content;
	private @Setter(lombok.AccessLevel.NONE) HashMap<KnownHeader, String> headers = new HashMap<>();

	public HttpResponse(Response response, Content content) {
		this(response);
		this.content = content;
	}

	public HttpResponse addHeader(KnownHeader header, String value) {
		this.headers.put(header, value);
		return this;
	}

}
