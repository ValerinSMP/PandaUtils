package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content.Multipart;

public class FormDataMultipart extends Multipart {

	public FormDataMultipart(String boundary) {
		super(MultipartType.FORM_DATA, boundary);
	}

}
