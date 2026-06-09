package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects;

import java.io.File;
import java.io.IOException;

public class PandaFolder extends PandaFile {

	public PandaFolder(String id, String path) {
		super(id, path);
	}

	@Override
	protected boolean createSystemFile(File file) throws IOException {
		return file.mkdirs();
	}

}
