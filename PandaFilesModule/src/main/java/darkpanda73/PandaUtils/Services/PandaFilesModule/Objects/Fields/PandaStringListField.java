package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects.Fields;

import java.util.List;

public class PandaStringListField extends PandaField<List<String>> {

	public PandaStringListField(String path, List<String> defaultContent) {
		super(path, defaultContent, Type.STRING_LIST);
	}

	public PandaStringListField(String path, String oldPath, List<String> defaultContent) {
		super(path, oldPath, defaultContent, Type.STRING_LIST);
	}

	public String[] toArray() {
		return getContent().toArray(new String[getContent().size()]);
	}

}
