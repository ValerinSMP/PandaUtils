package darkpanda73.PandaUtils.PandaHTTPServer.Objects.Content;

public class Empty extends Content {

	public Empty() {
		super(ContentType.EMPTY);
	}

	@Override
	public String toString() {
		return "Empty";
	}

	@Override
	public byte[] toBytes() {
		return new byte[0];
	}

	@Override
	public String getContentTypeHeader() {
		return "";
	}

}
