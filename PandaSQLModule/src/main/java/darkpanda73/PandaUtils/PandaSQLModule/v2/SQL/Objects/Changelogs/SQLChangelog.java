package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Changelogs;

import darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Objects.Connectors.SQLConnection;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableConsumer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class SQLChangelog {

	private @Getter int identifier;
	private @Getter String description;
	private FailableConsumer<SQLConnection> consumer;

	public void execute(SQLConnection sqlConnection) throws Throwable {
		consumer.accept(sqlConnection);
	}

}
