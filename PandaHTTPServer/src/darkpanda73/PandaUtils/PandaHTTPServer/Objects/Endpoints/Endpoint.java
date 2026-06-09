package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Method;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.DeleteEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.GetEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.HeadEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.OptionsEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.PostEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.PutEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import lombok.Data;

@Data
public abstract class Endpoint {

	@PandaInject
	private static HTTPServer httpServer;

	private String path;
	private Method[] allowedMethods = new Method[0];

	public Endpoint(String path) {
		this.path = path;
		List<Method> methods = new ArrayList<>();
		if (this instanceof GetEndpoint) {
			methods.add(Method.GET);
		}
		if (this instanceof DeleteEndpoint) {
			methods.add(Method.DELETE);
		}
		if (this instanceof PostEndpoint) {
			methods.add(Method.POST);
		}
		if (this instanceof PutEndpoint) {
			methods.add(Method.PUT);
		}
		if (this instanceof HeadEndpoint) {
			methods.add(Method.HEAD);
		}
		if (this instanceof OptionsEndpoint) {
			methods.add(Method.OPTIONS);
		}
		this.allowedMethods = methods.toArray(new Method[methods.size()]);
	}

	public abstract void processPreRequest(HTTPServer server, HttpInputStream input, String... args);

	public abstract boolean allowContent(HTTPServer server, HttpInputStream input, String... args);

}
