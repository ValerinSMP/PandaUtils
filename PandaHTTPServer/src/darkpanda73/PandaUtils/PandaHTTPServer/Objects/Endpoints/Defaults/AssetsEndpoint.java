package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Defaults;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaHTTPServer.Enums.Response;
import darkpanda73.PandaUtils.PandaHTTPServer.Exceptions.HttpResponse;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Endpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Endpoints.Methods.GetEndpoint;
import darkpanda73.PandaUtils.PandaHTTPServer.Objects.Streams.HttpInputStream;
import darkpanda73.PandaUtils.PandaHTTPServer.Threads.HTTPServer;
import darkpanda73.PandaUtils.PandaHTTPServer.Utils.ContentUtils;
import darkpanda73.PandaUtils.Utilities.Java.Bytes.ByteUtilities;

public class AssetsEndpoint extends Endpoint implements GetEndpoint {

	public AssetsEndpoint() {
		super("assets/*");
	}

	@Override
	public boolean allowContent(HTTPServer server, HttpInputStream input, String... args) {
		return false;
	}

	@Override
	public void processPreRequest(HTTPServer server, HttpInputStream input, String... args) {
		try {
			if (args.length > 0) {
				File f = new File(server.getMainFolderPath() + "\\assets"
						+ Arrays.stream(args).collect(Collectors.joining("\\", "\\", "")));

				if (!f.getAbsolutePath().startsWith(new File(server.getMainFolderPath()).getAbsolutePath())) {
					throw new HttpResponse(Response.FORBIDDEN);
				}

				if (f.exists()) {
					throw new HttpResponse(Response.OK,
							ContentUtils.loadContent(
									new String[] { Files.probeContentType(Paths.get(f.getAbsolutePath())) },
									ByteUtilities.readFileToBytes(f.getAbsolutePath())));
				}
			}
		} catch (Exception e) {
		}
		throw new HttpResponse(Response.NOT_FOUND);
	}

	@Override
	public void processGetRequest(HTTPServer server, HttpInputStream input, String... args) {
	}

}
