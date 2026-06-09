package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public class PandaLongField extends PandaField<Long> {

	public PandaLongField(String path, Long defaultContent) {
		super(path, defaultContent, Type.LONG);
	}

	public PandaLongField(String path, String oldPath, Long defaultContent) {
		super(path, oldPath, defaultContent, Type.LONG);
	}

}
