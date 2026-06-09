package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Objects.ConnectionData;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts.ConnectionData;
import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Enums.SQLType;

public class SQLiteConnectionData extends ConnectionData {

	private String path;

	public SQLiteConnectionData(String path) {
		super(SQLType.SQLITE);
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return path.replace("\\", "/");
	}

}
