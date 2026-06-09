package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods;

import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;

public interface PostEndpoint {

	public void processPostRequest(HTTPServer server, HttpInputStream input, String... args);

}
