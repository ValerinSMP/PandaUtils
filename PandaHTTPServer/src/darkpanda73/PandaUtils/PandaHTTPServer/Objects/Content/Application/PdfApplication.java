package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Application;

public class PdfApplication extends Application {

	public PdfApplication(byte[] content) {
		super(ApplicationType.JSON, content);
	}

}
