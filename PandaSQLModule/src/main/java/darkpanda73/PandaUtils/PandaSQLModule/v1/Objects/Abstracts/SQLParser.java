package darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.Abstracts;

import darkpanda73.PandaUtils.PandaSQLModule.v1.Objects.SQLObject;
import lombok.Data;

@Data
public abstract class SQLParser {

	protected When when = When.TABLES_CREATED;

	public abstract boolean parse(SQLObject api, String version, String prefix);

	public static enum When {
		TABLES_CREATED, DATA_REIMPORTED
	}

}
