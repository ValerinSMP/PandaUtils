package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.List;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;

public class YamlLongData extends YamlData<Long> {

	public YamlLongData(String name, Long data) {
		super(name, data);
	}

	public YamlLongData(YamlSection parent, String name, Long data) {
		super(parent, name, data);
	}

	@Override
	public Long getLong(Long def) {
		return data != null ? (Long) data.longValue() : def;
	}

	@Override
	public Double getDouble(Double def) {
		return data != null ? (Double) data.doubleValue() : def;
	}

	@Override
	public Boolean getBoolean(Boolean def) {
		return null;
	}

	@Override
	public List<String> getStringList(List<String> def) {
		return null;
	}

}
