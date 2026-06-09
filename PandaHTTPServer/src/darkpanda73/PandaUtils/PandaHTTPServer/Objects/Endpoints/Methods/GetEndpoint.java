package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;

public interface GetEndpoint {

	public void processGetRequest(HTTPServer server, HttpInputStream input, String... args);

}
