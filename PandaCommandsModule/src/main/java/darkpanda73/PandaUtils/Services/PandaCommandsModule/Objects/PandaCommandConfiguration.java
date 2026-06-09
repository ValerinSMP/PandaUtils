package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

@Data
@RequiredArgsConstructor
@Accessors(chain = true)
public class PandaCommandConfiguration {

	private @NonNull String id;
	private @NonNull String pathName;
	private boolean helpCommand = true;
	private @NonNull String defaultName;
	private @NonNull String defaultDescription;
	private String defaultPermission = "";
	private String defaultUsage = "";
	private String[] defaultAliases = {};
	private long cooldown = 0;

	private boolean customizableName = true;
	private boolean customizableDescription = true;
	private boolean customizablePermission = false;
	private boolean customizableUsage = false;
	private boolean customizableAliases = false;
	private boolean customizableCooldown = false;

}
