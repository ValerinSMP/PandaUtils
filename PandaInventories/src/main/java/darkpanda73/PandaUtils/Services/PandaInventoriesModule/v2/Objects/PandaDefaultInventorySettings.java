package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class PandaDefaultInventorySettings {

	private @Getter @Setter int version = 1;
	private @Getter Map<String, String> titles = new HashMap<>();
	private @Getter Map<String, Object> data = new HashMap<>();
	private @Getter List<PandaDefaultInventoryItem> items;
	private @Getter @Setter PandaDefaultInventoryCommand command;

	private @Getter @Setter int size;
	private @Getter @Setter boolean applyPlaceholderApi = false;

	public void setCommand(String command, String description) {
		this.setCommand(command, description, Collections.emptyList());
	}

	public void setCommand(String command, String description, List<String> aliases) {
		this.command = new PandaDefaultInventoryCommand(command, description, aliases);
	}

}
