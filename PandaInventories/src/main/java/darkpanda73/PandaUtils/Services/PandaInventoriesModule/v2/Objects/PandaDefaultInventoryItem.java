package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v2.Objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class PandaDefaultInventoryItem {

	private @Getter int version;
	private @Getter PandaInventoryItem item;

}
