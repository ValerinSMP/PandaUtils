package darkpanda73.PandaUtils.PandaJSON.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public class PandaJSONObject extends PandaJSONElement {

	private HashMap<String, PandaJSONElement> map = new LinkedHashMap<>();

	@Override
	public boolean isObject() {
		return true;
	}

	@Override
	public PandaJSONObject asObject() {
		return this;
	}

	public List<String> keys() {
		return new ArrayList<>(map.keySet());
	}

	public List<PandaJSONElement> values() {
		return new ArrayList<>(map.values());
	}

	public boolean has(String key) {
		return map.containsKey(key);
	}

	public Object get(String key) {
		return get(key, null);
	}

	public boolean isNull(String key) {
		PandaJSONElement el = map.get(key);
		return el == null || (el.isData() && el.asData().isNull());
	}

	public Object get(String key, Object defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).value() : (obj != null ? obj : defaultValue);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsString()
				: (obj != null ? obj.toString() : defaultValue);
	}

	public Boolean getBoolean(String key) {
		return getBoolean(key, null);
	}

	public Boolean getBoolean(String key, Boolean defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsBoolean()
				: (obj != null ? (Boolean) obj : defaultValue);
	}

	public Float getFloat(String key) {
		return getFloat(key, null);
	}

	public Float getFloat(String key, Float defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsFloat()
				: (obj != null ? (Float) obj : defaultValue);
	}

	public Double getDouble(String key) {
		return getDouble(key, null);
	}

	public Double getDouble(String key, Double defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsDouble()
				: (obj != null ? (Double) obj : defaultValue);
	}

	public Integer getInteger(String key) {
		return getInteger(key, null);
	}

	public Integer getInteger(String key, Integer defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsInteger()
				: (obj != null ? (Integer) obj : defaultValue);
	}

	public Long getLong(String key) {
		return getLong(key, null);
	}

	public Long getLong(String key, Long defaultValue) {
		Object obj = get(key);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).valueAsLong()
				: (obj != null ? (Long) obj : defaultValue);
	}

	public PandaJSONObject getObject(String key) {
		return getObject(key, null);
	}

	public PandaJSONObject getObject(String key, PandaJSONObject defaultValue) {
		Object value = get(key);
		if (value != null && !(value instanceof PandaJSONObject)) {
			throw new ClassCastException("Value is not a PandaJSONObject");
		}
		return value != null ? (PandaJSONObject) value : defaultValue;
	}

	public PandaJSONArray getArray(String key) {
		return getArray(key, null);
	}

	public PandaJSONArray getArray(String key, PandaJSONArray defaultValue) {
		Object value = get(key);
		if (value != null && !(value instanceof PandaJSONArray)) {
			throw new ClassCastException("Value is not a PandaJSONArray");
		}
		return value != null ? (PandaJSONArray) value : defaultValue;
	}

	public PandaJSONObject put(String key, Object data) {
		map.put(key, data instanceof PandaJSONElement ? (PandaJSONElement) data
				: new PandaJSONData(data instanceof PandaJSONData ? ((PandaJSONData) data).value() : data));
		return this;
	}

	public PandaJSONObject remove(String key) {
		map.remove(key);
		return this;
	}

	@Override
	public String toString(int level) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtilities.addSpaces(level, "{" + System.lineSeparator()));
		List<String> keys = keys();
		for (int i = 0; i < keys.size(); i++) {
			String key = keys.get(i);
			PandaJSONElement value = map.get(key);
			sb.append(StringUtilities.addSpaces(level + 2, "\"" + key + "\"") + ": ");

			if (value.isData()) {
				sb.append(value.toString());
			} else if (value.isObject() && value.asObject().values().size() == 0) {
				sb.append("{}");
			} else if (value.isArray() && value.asArray().values().size() == 0) {
				sb.append("[]");
			} else {
				sb.append(StringUtilities.removeLeadingChar(value.toString(level + 2), ' '));
			}

			if (i < keys.size() - 1)
				sb.append(",");
			sb.append(System.lineSeparator());
		}
		sb.append(StringUtilities.addSpaces(level, "}"));

		return sb.toString();
	}

}
