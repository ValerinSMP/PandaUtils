package darkpanda73.PandaUtils.PandaYaml.Objects;

import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class YamlComment extends YamlObject {

	private @Getter String comment;

	@Override
	public boolean isYamlComment() {
		return true;
	}

	@Override
	public YamlComment asYamlComment() {
		return this;
	}

	@Override
	public String toString(int height) {
		return StringUtils.repeat(' ', height * 2).concat(String.format("#%s", comment));
	}
}
