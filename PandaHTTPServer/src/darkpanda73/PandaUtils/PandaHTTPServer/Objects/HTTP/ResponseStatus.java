package darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Response;
import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Version;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ResponseStatus {

	private @NonNull Version version;
	private @NonNull Response response;

}
