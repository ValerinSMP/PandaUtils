package darkpanda73.PandaUtils.Services.PandaPermissionsModule.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class PandaParametizedPermission extends PandaCustomizablePermission {

	private static final Pattern PARAMETER_PATTERN = Pattern.compile("\\{([a-z0-9]+)\\}");

	public PandaParametizedPermission(String path, String defaultPermission) {
		super(path, defaultPermission);
	}

	public HashMap<String, HashMap<String, String>> findPermissions(Player pl) {
		String permission = getPermission().toLowerCase();
		Matcher matcher = PARAMETER_PATTERN.matcher(permission);

		int lastEnd = 0;
		List<String> parameters = new ArrayList<>();
		List<String> split = new ArrayList<>();
		while (matcher.find()) {
			parameters.add(matcher.group(1));
			split.add(permission.substring(lastEnd, matcher.start()));
			lastEnd = matcher.end();
		}

		if (lastEnd <= permission.length()) {
			split.add(permission.substring(lastEnd));
		}

		Pattern permissionPattern = Pattern.compile(
				split.stream().collect(Collectors.joining("([0-9a-zA-Z_\\+\\-#]+)")).replaceAll("\\.", "\\\\."));

		HashMap<String, HashMap<String, String>> foundPermissions = new HashMap<>();
		pl.getEffectivePermissions().stream().forEach(playerPermission -> {
			if (playerPermission.getValue()) {
				Matcher permissionMatcher = permissionPattern.matcher(playerPermission.getPermission().toLowerCase());
				if (permissionMatcher.find()) {
					HashMap<String, String> values = new HashMap<>();
					for (int i = 0; i < permissionMatcher.groupCount(); i++) {
						values.put(parameters.get(i), permissionMatcher.group(i + 1));
					}
					foundPermissions.put(playerPermission.getPermission(), values);
				}
			}
		});
		return foundPermissions;
	}

	@Override
	public boolean hasPermission(Player pl) {
		return false;
	}

	public boolean hasPermission(Player pl, Parameter... parameters) {
		String permission = getPermission();
		Matcher matcher = PARAMETER_PATTERN.matcher(permission);

		int lastEnd = 0;
		List<String> permissionSplit = new ArrayList<>();
		while (matcher.find()) {
			permissionSplit.add(permission.substring(lastEnd, matcher.start()));
			for (Parameter parameter : parameters) {
				if (parameter.getKey().equalsIgnoreCase(matcher.group(1))) {
					permissionSplit.add(parameter.getValue());
					break;
				}
			}
			lastEnd = matcher.end();
		}

		if (lastEnd <= permission.length()) {
			permissionSplit.add(permission.substring(lastEnd));
		}

		return pl.hasPermission(permissionSplit.stream().collect(Collectors.joining()));
	}

	@Getter
	@AllArgsConstructor
	public static class Parameter {

		private String key;
		private String value;

		public static Parameter of(String key, String value) {
			return new Parameter(key, value);
		}
	}
}
