package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public class PandaBooleanField extends PandaField<Boolean> {

	public PandaBooleanField(String path, Boolean defaultContent) {
		super(path, defaultContent, Type.BOOLEAN);
	}

	public PandaBooleanField(String path, String oldPath, Boolean defaultContent) {
		super(path, oldPath, defaultContent, Type.BOOLEAN);
	}

	public boolean isTrue() {
		return Boolean.TRUE.equals(getContent());
	}

	public boolean isFalse() {
		return Boolean.FALSE.equals(getContent());
	}

}
