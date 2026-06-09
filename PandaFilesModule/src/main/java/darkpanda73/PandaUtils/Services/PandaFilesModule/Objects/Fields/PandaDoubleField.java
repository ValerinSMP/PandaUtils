package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public class PandaDoubleField extends PandaField<Double> {

	public PandaDoubleField(String path, Double defaultContent) {
		super(path, defaultContent, Type.DOUBLE);
	}

	public PandaDoubleField(String path, String oldPath, Double defaultContent) {
		super(path, oldPath, defaultContent, Type.DOUBLE);
	}

}
