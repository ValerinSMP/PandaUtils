package darkpanda73.PandaUtils.Services.PandaFilesModule.Objects;

import java.io.File;
import java.io.IOException;

import lombok.Getter;

public class PandaFile {

	private @Getter String id;
	private @Getter File file;

	public PandaFile(String id, String path) {
		this.id = id;
		this.file = new File(path);
	}

	public Result create() {
		boolean isNew = !file.exists();

		if (!file.exists()) {
			try {
				if (!createSystemFile(file)) {
					return Result.FAILED;
				}
			} catch (IOException e) {
				System.out.println(String.format("Unable to create file '%s':", getFile().getPath()));
				e.printStackTrace();
				return Result.FAILED;
			}
		}

		Result finalResult = Result.SUCCESS;

		if (isNew) {
			Result result = onCreate();

			if (result == Result.FAILED) {
				return result;
			}

			finalResult = result;
		}

		Result result = onFinish();

		if (result == Result.FAILED) {
			return result;
		}

		finalResult = result == Result.SUCCESS ? finalResult : result;

		return finalResult;
	}

	public Result onCreate() {
		return Result.SUCCESS;
	}

	public Result onFinish() {
		return Result.SUCCESS;
	}

	public static enum Result {
		SUCCESS, BACKUP, FAILED;
	}

	protected boolean createSystemFile(File file) throws IOException {
		return file.createNewFile();
	}
}
