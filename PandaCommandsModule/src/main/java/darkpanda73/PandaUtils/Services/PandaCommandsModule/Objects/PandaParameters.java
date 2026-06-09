package darkpanda73.PandaUtils.Services.PandaCommandsModule.Objects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;
import lombok.Data;
import lombok.NonNull;

@Data
public class PandaParameters {

	private @NonNull String commandAlias;
	private @NonNull List<String> parameters;
	private List<String> unprocessedParameters = new ArrayList<>();
	private List<String> keyOnlyParameters = new ArrayList<>();
	private List<Pair<String, String>> keyValueParameters = new ArrayList<>();

	public static PandaParameters from(String commandAlias, List<String> registeredKeyOnlyParameters,
			List<String> registeredKeyValueParameters, String[] paramsToProcess) {
		PandaParameters parameters = new PandaParameters(commandAlias, Arrays.asList(paramsToProcess));

		if (!registeredKeyOnlyParameters.isEmpty() || !registeredKeyValueParameters.isEmpty()) {
			for (int i = 0; i < paramsToProcess.length; i++) {
				String param = paramsToProcess[i];
				if (registeredKeyOnlyParameters.contains(param)) {
					parameters.getKeyOnlyParameters().add(param);
				} else if (registeredKeyValueParameters.contains(param)) {
					if (paramsToProcess.length > i + 1) {
						String value = paramsToProcess[++i];

						if (!registeredKeyOnlyParameters.contains(value)
								&& !registeredKeyValueParameters.contains(value)) {
							if (value.toCharArray()[0] == '"') {
								while (++i < paramsToProcess.length && value.toCharArray()[value.length() - 1] != '"') {
									value += paramsToProcess[i];
								}
							}
							parameters.getKeyValueParameters()
									.add(Pair.of(param, value.substring(value.toCharArray()[0] == '"' ? 1 : 0,
											value.toCharArray()[0] == '"' ? value.length() - 1 : value.length())));
						} else {
							parameters.getKeyValueParameters().add(Pair.of(param, null));
							i--;
						}
					} else {
						parameters.getKeyValueParameters().add(Pair.of(param, null));
					}
				} else {
					parameters.getUnprocessedParameters().add(param);
				}
			}
		}

		return parameters;
	}

}
