package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects.PandaCommand;
import lombok.Getter;
import lombok.Setter;

public class PandaInventorySettings {

	// Constants

	public static final String VERSION_KEY = "Version";
	public static final String TITLES_KEY = "Titles";
	public static final String DATA_KEY = "Data";
	public static final String SIZE_KEY = "Size";
	public static final String ITEMS_KEY = "Items";
	public static final String APPLY_PLACEHOLDERAPI_KEY = "Apply-PlaceholderAPI";

	public static final String TITLES_DEFAULT_KEY = "Default";

	public static final String COMMAND_NAME_KEY = "Command.Name";
	public static final String COMMAND_DESCRIPTION_KEY = "Command.Description";
	public static final String COMMAND_ALIASES_KEY = "Command.Aliases";

	private @Getter String id;
	private @Getter int version;

	private @Getter Map<String, String> titles = new HashMap<>();
	private @Getter Map<String, Object> data = new HashMap<>();
	private @Getter List<PandaInventoryItem> items;
	private @Getter @Setter PandaCommand command;

	private @Getter @Setter int size;
	private @Getter @Setter boolean applyPlaceholderApi = false;

	public PandaInventorySettings(String id, int version) {
		this.id = id;
		this.version = version;
	}

	public String getTitle() {
		return this.getTitle(TITLES_DEFAULT_KEY);
	}

	public String getTitle(String id) {
		return this.titles.getOrDefault(id.toLowerCase(), "Title");
	}

}
