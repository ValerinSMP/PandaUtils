package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Enums.SQLType;

public abstract class ConnectionData {

	private SQLType type;

	public ConnectionData(SQLType type) {
		this.type = type;
	}

	public SQLType getType() {
		return type;
	}

}
