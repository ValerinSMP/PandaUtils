package darkpanda73.PandaUtils.PandaYaml.Objects;

import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;

public class YamlObject {

	public boolean isYamlNamedObject() {
		return false;
	}

	public YamlNamedObject asYamlNamedObject() {
		return null;
	}

	public boolean isYamlData() {
		return false;
	}

	public YamlData<?> asYamlData() {
		return null;
	}

	public boolean isYamlSection() {
		return false;
	}

	public YamlSection asYamlSection() {
		return null;
	}

	public boolean isYamlComment() {
		return false;
	}

	public YamlComment asYamlComment() {
		return null;
	}

	public boolean isYamlSpace() {
		return false;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	public String toString(int height) {
		return StringUtils.repeat(' ', height * 2).concat(toString());
	}

}
