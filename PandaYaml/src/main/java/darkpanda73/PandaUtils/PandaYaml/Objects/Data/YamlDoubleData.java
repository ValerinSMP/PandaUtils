package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.List;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;

public class YamlDoubleData extends YamlData<Double> {

	public YamlDoubleData(String name, Double data) {
		super(name, data);
	}

	public YamlDoubleData(YamlSection parent, String name, Double data) {
		super(parent, name, data);
	}

	@Override
	public Double getDouble(Double def) {
		return data != null ? (Double) data.doubleValue() : def;
	}

	@Override
	public Long getLong(Long def) {
		return data != null ? (Long) data.longValue() : def;
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
