package darkpanda73.PandaUtils.Services.PandaInventoriesModule.v1.Objects.ChestInventory;

import java.util.HashMap;
import java.util.LinkedHashMap;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class DefaultItem {

	// Item variables
	private @Getter @NonNull String id;

	// Item information from file
	private @Getter HashMap<String, Object> data = new LinkedHashMap<>();
	private @Getter @Setter int version = 1;

}
