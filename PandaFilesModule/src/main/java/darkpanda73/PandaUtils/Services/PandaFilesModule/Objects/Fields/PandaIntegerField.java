package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public class PandaIntegerField extends PandaField<Integer> {

	public PandaIntegerField(String path, Integer defaultContent) {
		super(path, defaultContent, Type.INTEGER);
	}

	public PandaIntegerField(String path, String oldPath, Integer defaultContent) {
		super(path, oldPath, defaultContent, Type.INTEGER);
	}

}
