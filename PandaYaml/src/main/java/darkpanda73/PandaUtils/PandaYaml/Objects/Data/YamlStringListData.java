package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;

public class YamlStringListData extends YamlData<List<String>> {

	public YamlStringListData(String name, List<String> data) {
		super(name, data);
	}

	public YamlStringListData(YamlSection parent, String name, List<String> data) {
		super(parent, name, data);
	}

	@Override
	public List<String> getStringList(List<String> def) {
		return data != null ? new ArrayList<>(data) : def;
	}

	@Override
	public Double getDouble(Double def) {
		return null;
	}

	@Override
	public Long getLong(Long def) {
		return null;
	}

	@Override
	public Boolean getBoolean(Boolean def) {
		return null;
	}

	@Override
	public String toString(int height) {
		StringBuilder builder = new StringBuilder();

		String spaces = StringUtils.repeat(' ', height * 2);
		String format = spaces.concat("%s: %s");

		if (data != null) {
			List<String> list = getStringList();

			if (!list.isEmpty()) {
				builder.append(String.format(format, getName(), ""));
				list.forEach(line -> {
					List<String> splits = new ArrayList<>();

					int pos = 64;
					while ((pos = line.indexOf(' ', pos)) != -1) {
						if (pos != line.length() - 1 && line.charAt(pos + 1) == ' ') {
							pos++;
						} else {
							splits.add(line.substring(0, pos).replaceAll("\"", "\\\\\""));
							line = line.substring(pos);
							pos = 64;
						}
					}
					splits.add(line.replaceAll("\"", "\\\\\""));

					builder.append(String.format("%s- %s", System.lineSeparator().concat(spaces),
							splits.stream().collect(Collectors.joining(System.lineSeparator().concat(spaces).concat("  "), "\"", "\""))));
				});
			} else {
				builder.append(String.format(format, getName(), "[]"));
			}
		} else {
			builder.append(String.format(format, getName(), "null"));
		}

		return builder.toString();
	}

}
