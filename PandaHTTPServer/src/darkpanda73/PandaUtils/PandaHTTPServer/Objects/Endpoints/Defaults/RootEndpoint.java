package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Defaults;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Response;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpException;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpResponse;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Endpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.GetEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;
import darkpanda73.PandaUtils.PandaHTTPServer.Utils.ContentUtils;
import darkpanda73.PandaUtils.Utilities.Java.Bytes.ByteUtilities;

public class RootEndpoint extends Endpoint implements GetEndpoint {

	public RootEndpoint() {
		super("*");
	}

	@Override
	public boolean allowContent(HTTPServer server, HttpInputStream input, String... args) {
		return false;
	}

	@Override
	public void processPreRequest(HTTPServer server, HttpInputStream input, String... args) {
		try {
			File f = new File(server.getMainFolderPath()
					+ (args.length != 0 ? Arrays.stream(args).collect(Collectors.joining("\\", "\\", "")) : ""));

			if (!f.getAbsolutePath().startsWith(new File(server.getMainFolderPath()).getAbsolutePath())) {
				throw new HttpResponse(Response.FORBIDDEN);
			}

			if (!f.exists()) {
				f = new File(f.getPath() + ".html");
			}

			if (f.exists()) {
				if (f.isDirectory()) {
					f = new File(f.getAbsoluteFile() + "\\index.html");
					if (!f.exists()) {
						f = null;
					}
				}
				if (f != null) {
					throw new HttpResponse(Response.OK,
							ContentUtils.loadContent(
									new String[] { Files.probeContentType(Paths.get(f.getAbsolutePath())) },
									ByteUtilities.readFileToBytes(f.getAbsolutePath())));
				}
			}
		} catch (HttpException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new HttpResponse(Response.NOT_FOUND);
	}

	@Override
	public void processGetRequest(HTTPServer server, HttpInputStream input, String... args) {
	}

}
