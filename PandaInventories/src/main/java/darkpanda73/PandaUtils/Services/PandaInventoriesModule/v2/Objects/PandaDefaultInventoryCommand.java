package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PandaDefaultInventoryCommand {

	private @Getter String name;
	private @Getter String description;
	private @Getter List<String> aliases;

}
