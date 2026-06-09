package darkpanda73.PandaUtils.PandaHTTPServer.Objects.HTTP;

import java.util.HashMap;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Method;
import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Version;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class RequestMethod {

	private @NonNull Method method;
	private @NonNull String path;
	private @NonNull Version version;
	private HashMap<String, String> parameters = new HashMap<>();

}
