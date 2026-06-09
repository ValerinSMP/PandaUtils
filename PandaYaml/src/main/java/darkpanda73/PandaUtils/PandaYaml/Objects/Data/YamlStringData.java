package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;

public class YamlStringData extends YamlData<String> {

	public YamlStringData(String name, String data) {
		super(name, data);
	}

	public YamlStringData(YamlSection parent, String name, String data) {
		super(parent, name, data);
	}

	@Override
	public Double getDouble(Double def) throws ClassCastException {
		try {
			return data != null ? (Double) Double.parseDouble(data) : def;
		} catch (NumberFormatException e) {
			return def;
		}
	}

	@Override
	public Long getLong(Long def) {
		try {
			return data != null ? (Long) Long.parseLong(data) : def;
		} catch (NumberFormatException e) {
			return def;
		}
	}

	@Override
	public Boolean getBoolean(Boolean def) {
		return data != null ? (Boolean) Boolean.parseBoolean(data) : def;
	}

	@Override
	public List<String> getStringList(List<String> def) {
		return data != null ? Arrays.asList(data.toString()) : def;
	}

	@Override
	public String toString(int height) {
		StringBuilder builder = new StringBuilder();

		String spaces = StringUtils.repeat(' ', height * 2);
		String format = spaces.concat("%s: %s");

		if (data != null) {
			String string = getString();

			List<String> splits = new ArrayList<>();

			int pos = 64;
			while ((pos = string.indexOf(' ', pos)) != -1) {
				if (pos != string.length() - 1 && string.charAt(pos + 1) == ' ') {
					pos++;
				} else {
					splits.add(string.substring(0, pos).replaceAll("\"", "\\\\\""));
					string = string.substring(pos + 1);
					pos = 64;
				}
			}
			splits.add(string.replaceAll("\"", "\\\\\""));

			builder.append(String.format(format, getName(), splits.stream()
					.collect(Collectors.joining(System.lineSeparator().concat(spaces).concat("  "), "\"", "\""))));
		} else {
			builder.append(String.format(format, getName(), "null"));
		}

		return builder.toString();
	}

}
