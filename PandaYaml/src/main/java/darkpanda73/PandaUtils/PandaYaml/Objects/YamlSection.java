package darkpanda73.PandaUtils.PandaYaml.Objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import darkpanda73.PandaUtils.PandaYaml.Objects.Data.YamlData;
import darkpanda73.PandaUtils.PandaYaml.Utils.StringUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public class YamlSection extends YamlNamedObject {

	private @Getter @EqualsAndHashCode.Exclude List<YamlObject> childs = new LinkedList<>();

	public YamlSection(String name) {
		super(name);
	}

	public YamlSection(YamlSection parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean isYamlSection() {
		return true;
	}

	@Override
	public YamlSection asYamlSection() {
		return this;
	}

	public void clear() {
		this.childs.clear();
	}

	public void space() {
		this.childs.add(new YamlSpace());
	}

	public void comment(String comment) {
		this.childs.add(new YamlComment(comment));
	}

	public boolean has(@NonNull String key) {
		return get(key) != null;
	}

	public Map<String, YamlData<?>> set(@NonNull Map<String, Object> values) {
		return values.entrySet().stream()
				.collect(Collectors.toMap(Entry::getKey, entry -> set(entry.getKey(), entry.getValue())));
	}

	public YamlData<?> set(@NonNull String key, Object value) {
		String[] keySplit = key.split("\\.");
		return set(key, YamlData.fromObject(keySplit[keySplit.length - 1], value));
	}

	@SuppressWarnings("unchecked")
	private <T extends YamlData<R>, R> T set(@NonNull String key, T value) {
		if (key.isEmpty()) {
			return null;
		}

		String[] keySplit = key.split("\\.", 2);

		Iterator<YamlObject> iterator = childs.iterator();
		YamlObject obj = null;
		while (iterator.hasNext()) {
			obj = iterator.next();
			if (obj.isYamlNamedObject() && obj.asYamlNamedObject().getName().equalsIgnoreCase(keySplit[0])) {
				if (keySplit.length > 1) {
					if (obj.isYamlSection()) {
						return obj.asYamlSection().set(keySplit[1], value);
					}
				} else {
					if (obj.isYamlData()) {
						if (value.getClass().equals(obj.getClass())) {
							((T) obj).setData(value.getData());
							return (T) obj.asYamlData();
						}
					}
				}
				iterator.remove();
			}
		}

		if (keySplit.length > 1) {
			YamlSection section = new YamlSection(this, keySplit[0]);

			childs.add(section);

			return section.set(keySplit[1], value);
		} else {
			value.setParent(this);

			childs.add(value);

			return value;
		}
	}

	public void add(YamlObject child) {
		this.childs.add(child);
	}

	public boolean remove(@NonNull String key) {
		if (key.isEmpty()) {
			return false;
		}

		String[] keySplit = key.split("\\.", 2);

		Iterator<YamlObject> iterator = childs.iterator();
		YamlObject obj = null;
		while (iterator.hasNext()) {
			obj = iterator.next();
			if (obj.isYamlNamedObject() && obj.asYamlNamedObject().getName().equalsIgnoreCase(keySplit[0])) {
				if (keySplit.length > 1) {
					if (obj.isYamlSection()) {
						obj.asYamlSection().remove(keySplit[1]);
					}
				} else {
					iterator.remove();
				}
				return true;
			}
		}

		return false;
	}

	public YamlData<?> getData(@NonNull String key) {
		return getDataOrDefault(key, null);
	}

	public YamlData<?> getDataOrDefault(@NonNull String key, Object defValue) {
		YamlNamedObject object = get(key);

		if (object != null && object.isYamlData()) {
			return object.asYamlData();
		}

		return defValue instanceof YamlData ? (YamlData<?>) defValue
				: (defValue != null ? YamlData.fromObject(key, defValue) : null);
	}

	public YamlSection getSection(@NonNull String key) {
		YamlNamedObject object = get(key);

		if (object != null && object.isYamlSection()) {
			return object.asYamlSection();
		}

		return null;
	}

	public YamlSection getSectionOrDefault(@NonNull String key) {
		YamlNamedObject object = get(key);

		if (object != null && object.isYamlSection()) {
			return object.asYamlSection();
		}

		return new YamlSection(this, key);
	}

	public YamlNamedObject get(@NonNull String key) {
		return get(key, (YamlNamedObject) null);
	}

	public YamlNamedObject getOrDefault(@NonNull String key, Object defValue) {
		return defValue instanceof YamlNamedObject ? get(key, (YamlNamedObject) defValue)
				: get(key, defValue instanceof YamlNamedObject ? (YamlNamedObject) defValue
						: (defValue != null ? YamlData.fromObject(key, defValue) : null));
	}

	public YamlNamedObject get(@NonNull String key, YamlNamedObject defValue) {
		if (defValue != null) {
			defValue.setParent(this);
		}

		if (key.isEmpty()) {
			return defValue;
		}

		String[] keySplit = key.split("\\.", 2);

		if (keySplit[0].isEmpty()) {
			return defValue;
		}

		YamlNamedObject yamlNamedObject = (YamlNamedObject) childs.stream()
				.filter(yamlObject -> yamlObject instanceof YamlNamedObject
						&& ((YamlNamedObject) yamlObject).getName().equalsIgnoreCase(keySplit[0]))
				.findFirst().orElse(null);
		if (yamlNamedObject != null && yamlNamedObject instanceof YamlNamedObject) {
			if (keySplit.length > 1) {
				if (yamlNamedObject.isYamlSection()) {
					return yamlNamedObject.asYamlSection().get(keySplit[1]);
				} else {
					return defValue;
				}
			}
			return yamlNamedObject;
		}

		return defValue;
	}

	public HashMap<String, Object> toMap() {
		HashMap<String, Object> map = new LinkedHashMap<>();

		getAllNamedChilds().forEach(child -> map.put(child.asYamlData().toPath(this), child.asYamlData().getData()));

		return map;
	}

	public List<YamlNamedObject> getNamedChilds() {
		return getChilds().stream().filter(YamlObject::isYamlNamedObject).map(YamlObject::asYamlNamedObject)
				.collect(Collectors.toList());
	}

	public List<YamlData<?>> getAllNamedChilds() {
		List<YamlData<?>> childs = new ArrayList<>();

		getChilds().stream().filter(YamlObject::isYamlNamedObject).forEach(object -> {
			if (object.isYamlData()) {
				childs.add(object.asYamlData());
			} else if (object.isYamlSection()) {
				childs.addAll(object.asYamlSection().getAllNamedChilds());
			}
		});

		return childs;
	}

	@Override
	public String toString() {
		return toString(0);
	}

	@Override
	public String toString(int height) {
		StringBuilder builder = new StringBuilder();

		String spaces = StringUtils.repeat(' ', height * 2);
		String format = spaces.concat("%s: ");

		builder.append(String.format(format, getName()));

		if (childs.size() > 0) {
			childs.forEach(child -> builder.append(System.lineSeparator().concat(child.toString(height + 1))));
		}

		return builder.toString();
	}

}
