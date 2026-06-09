package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

public abstract class PandaThrowableField<T extends Throwable> extends PandaStringField {

	public PandaThrowableField(String path, String defaultContent) {
		super(path, defaultContent);
	}

	public PandaThrowableField(String path, String oldPath, String defaultContent) {
		super(path, oldPath, defaultContent);
	}

	public T generateException() {
		return generateException(null);
	}

	public abstract T generateException(Throwable e);

}
