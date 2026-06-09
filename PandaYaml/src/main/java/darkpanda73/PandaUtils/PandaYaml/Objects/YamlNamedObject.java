package darkpanda73.PandaUtils.PandaYaml.Objects;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@AllArgsConstructor
@ToString(callSuper = false)
@EqualsAndHashCode(callSuper = false)
public class YamlNamedObject extends YamlObject {

	private @Getter @Setter YamlSection parent;
	private @NonNull @Getter String name;

	public String toPath() {
		return toPath(null);
	}

	public String toPath(YamlSection ignoreParent) {
		return (this.parent != null && this.parent.getParent() != null && this.parent != ignoreParent)
				? String.format("%s.%s", this.parent.toPath(ignoreParent), this.name)
				: this.name;
	}

	@Override
	public boolean isYamlNamedObject() {
		return true;
	}

	@Override
	public YamlNamedObject asYamlNamedObject() {
		return this;
	}

}
