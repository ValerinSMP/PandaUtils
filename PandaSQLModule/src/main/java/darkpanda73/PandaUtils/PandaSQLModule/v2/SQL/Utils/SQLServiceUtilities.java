package darkpanda73.PandaUtils.PandaSQLModule.v2.SQL.Utils;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaInject;
import darkpanda73.PandaUtils.PandaSQLModule.v2.Services.PandaSQLService;
import lombok.Getter;

public class SQLServiceUtilities {

	@PandaInject
	private @Getter static PandaSQLService instance;

	public static boolean isPrintQueriesInConsoleEnabled() {
		return instance != null && instance.getSqlPrintQueriesInConsole().isTrue();
	}

}
