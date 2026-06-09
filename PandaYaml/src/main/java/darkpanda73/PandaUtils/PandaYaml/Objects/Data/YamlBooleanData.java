package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.List;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;

public class YamlBooleanData extends YamlData<Boolean> {

	public YamlBooleanData(String name, Boolean data) {
		super(name, data);
	}

	public YamlBooleanData(YamlSection parent, String name, Boolean data) {
		super(parent, name, data);
	}

	@Override
	public Boolean getBoolean(Boolean def) {
		return data != null ? data.booleanValue() : def;
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
	public List<String> getStringList(List<String> def) {
		return null;
	}

}
