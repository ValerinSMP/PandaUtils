package darkpanda73.PandaUtils.PandaYaml.Objects.Data;

import java.util.List;

import darkpanda73.PandaUtils.PandaYaml.Objects.YamlNamedObject;
import darkpanda73.PandaUtils.PandaYaml.Objects.YamlSection;
import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;
import lombok.NonNull;
import lombok.Setter;

public abstract class YamlData<T extends Object> extends YamlNamedObject {

	protected @Setter T data;

	public YamlData(String name, T data) {
		this(null, name, data);
	}

	public YamlData(YamlSection parent, String name, T data) {
		super(parent, name);

		this.data = data;
	}

	public String getString() {
		return this.getString(null);
	}

	public String getString(String def) {
		return data != null ? data.toString() : def;
	}

	public Short getShort() {
		return this.getShort(null);
	}

	public Short getShort(Short def) {
		Long value = this.getLong();

		return value != null ? (Short) value.shortValue() : def;
	}

	public Float getFloat() {
		return this.getFloat(null);
	}

	public Float getFloat(Float def) {
		Double value = this.getDouble();

		return value != null ? (Float) value.floatValue() : def;
	}

	public Integer getInteger() {
		return this.getInteger(null);
	}

	public Integer getInteger(Integer def) {
		Long value = this.getLong();

		return value != null ? (Integer) value.intValue() : def;
	}

	public Double getDouble() {
		return this.getDouble(null);
	}

	public Long getLong() {
		return this.getLong(null);
	}

	public Boolean getBoolean() {
		return this.getBoolean(false);
	}

	public List<String> getStringList() {
		return this.getStringList(null);
	}

	public T getData() {
		return getData(null);
	}

	public T getData(T def) {
		return data != null ? data : def;
	}

	public Class<?> getDataType() {
		return data != null ? data.getClass() : null;
	}

	@Override
	public boolean isYamlData() {
		return true;
	}

	@Override
	public YamlData<T> asYamlData() {
		return this;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override
	public String toString(int height) {
		StringBuilder builder = new StringBuilder();

		String spaces = StringUtils.repeat(' ', height * 2);
		String format = spaces.concat("%s: %s");

		if (data != null) {
			builder.append(String.format(format, getName(), getString()));
		} else {
			builder.append(String.format(format, getName(), "null"));
		}

		return builder.toString();
	}

	public abstract Double getDouble(Double def);

	public abstract Long getLong(Long def);

	public abstract Boolean getBoolean(Boolean def);

	public abstract List<String> getStringList(List<String> def);

	public static YamlData<?> empty() {
		return empty("");
	}

	public static YamlData<?> empty(@NonNull String name) {
		return new YamlStringData(name, null);
	}

	@SuppressWarnings("unchecked")
	public static YamlData<?> fromObject(String name, Object value) {
		if (value != null) {
			if (value instanceof YamlData<?>) {
				return (YamlData<?>) value;
			} else if (value instanceof String) {
				return new YamlStringData(name, (String) value);
			} else if (value instanceof Double) {
				return new YamlDoubleData(name, (Double) value);
			} else if (value instanceof Float) {
				return new YamlDoubleData(name, ((Float) value).doubleValue());
			} else if (value instanceof Long) {
				return new YamlLongData(name, (Long) value);
			} else if (value instanceof Integer) {
				return new YamlLongData(name, ((Integer) value).longValue());
			} else if (value instanceof Short) {
				return new YamlLongData(name, ((Short) value).longValue());
			} else if (value instanceof Boolean) {
				return new YamlBooleanData(name, (Boolean) value);
			} else if (value instanceof List) {
				try {
					return new YamlStringListData(name, (List<String>) value);
				} catch (Exception e) {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return new YamlStringData(name, null);
		}
	}

}
