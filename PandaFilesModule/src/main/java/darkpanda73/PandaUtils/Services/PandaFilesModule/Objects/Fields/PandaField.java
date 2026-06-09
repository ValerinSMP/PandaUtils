package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

import java.util.Arrays;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public abstract class PandaField<T> {

	private String path;

	private String oldPath;

	private Type type;

	private T defaultContent;

	private T content;

	private boolean optional;

	public PandaField(String path, T defaultContent, Type type) {
		this(path, path, defaultContent, type);
	}

	public PandaField(String path, String oldPath, T defaultContent, Type type) {
		this.path = path;
		this.oldPath = oldPath;
		this.defaultContent = defaultContent;
		this.type = type;
	}

	@SuppressWarnings("unchecked")
	public void setObjectContent(Object content) {
		try {
			T result = null;
			switch (type) {
			case BOOLEAN:
				result = (T) ((Boolean) Boolean.parseBoolean(content.toString()));
				break;
			case DOUBLE:
				result = (T) ((Double) Double.parseDouble(content.toString()));
				break;
			case INTEGER:
				result = (T) ((Integer) Integer.parseInt(content.toString()));
				break;
			case LONG:
				result = (T) ((Long) Long.parseLong(content.toString()));
				break;
			case STRING:
				result = (T) content.toString();
				break;
			case STRING_LIST:
				result = List.class.isAssignableFrom(content.getClass()) ? (T) content
						: (T) Arrays.asList(content.toString());
				break;
			}
			setContent(result);
		} catch (Exception e) {
		}
	}

	public T getContent() {
		return (hasContent() ? content : defaultContent);
	}

	public boolean hasContent() {
		return content != null;
	}

	@Override
	public String toString() {
		return String.valueOf(getContent());
	}

	public static enum Type {
		INTEGER, LONG, DOUBLE, BOOLEAN, STRING, STRING_LIST
	}
}
