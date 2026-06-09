package darkpanda73.PandaUtils.PandaJSON.Objects;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.Utilities.Java.String.StringUtilities;

public class PandaJSONArray extends PandaJSONElement {

	private List<PandaJSONElement> array = new ArrayList<>();

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public PandaJSONArray asArray() {
		return this;
	}

	public List<PandaJSONElement> values() {
		return array;
	}

	public Object get(int index) {
		return get(index, null);
	}

	public Object get(int index, Object defaultValue) {
		Object obj = array.get(index);
		return obj instanceof PandaJSONData ? ((PandaJSONData) obj).value() : (obj != null ? obj : defaultValue);
	}

	public PandaJSONObject getObject(int index) {
		return getObject(index, null);
	}

	public PandaJSONObject getObject(int index, PandaJSONObject defaultValue) {
		Object value = get(index);
		if (value != null && !(value instanceof PandaJSONObject)) {
			throw new ClassCastException("Value is not a PandaJSONObject");
		}
		return value != null ? (PandaJSONObject) value : defaultValue;
	}

	public PandaJSONArray getArray(int index) {
		return getArray(index, null);
	}

	public PandaJSONArray getArray(int index, PandaJSONArray defaultValue) {
		Object value = get(index);
		if (value != null && !(value instanceof PandaJSONArray)) {
			throw new ClassCastException("Value is not a PandaJSONArray");
		}
		return value != null ? (PandaJSONArray) value : defaultValue;
	}

	public PandaJSONArray add(Object... data) {
		for (Object entry : data) {
			if (entry instanceof PandaJSONElement) {
				array.add((PandaJSONElement) entry);
			} else {
				array.add(new PandaJSONData(entry));
			}
		}
		return this;
	}

	public PandaJSONArray remove(int index) {
		array.remove(index);
		return this;
	}

	@Override
	public String toString(int level) {
		StringBuilder sb = new StringBuilder();
		if (array.size() == 0) {
			sb.append(StringUtilities.addSpaces(level, "[]"));
		} else {
			sb.append(StringUtilities.addSpaces(level, "[" + System.lineSeparator()));
			for (int i = 0; i < array.size(); i++) {
				sb.append(array.get(i).toString(level + 2));
				if (i < array.size() - 1)
					sb.append(",");
				sb.append(System.lineSeparator());
			}
			sb.append(StringUtilities.addSpaces(level, "]"));
		}
		return sb.toString();
	}

}
