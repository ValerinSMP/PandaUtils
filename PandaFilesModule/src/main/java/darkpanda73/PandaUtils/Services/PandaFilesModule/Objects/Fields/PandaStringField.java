package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public class PandaStringField extends PandaField<String> {

	public PandaStringField(String path, String defaultContent) {
		super(path, defaultContent, Type.STRING);
	}

	public PandaStringField(String path, String oldPath, String defaultContent) {
		super(path, oldPath, defaultContent, Type.STRING);
	}

}
