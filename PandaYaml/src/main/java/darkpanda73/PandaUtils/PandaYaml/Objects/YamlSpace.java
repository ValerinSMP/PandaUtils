package darkpanda73.PandaUtils.PandaYaml.Objects;

import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;

public class YamlSpace extends YamlObject {

	@Override
	public boolean isYamlSpace() {
		return true;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override
	public String toString(int height) {
		return StringUtils.repeat(' ', height * 2);
	}

}
