package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.PersistentDataContainer;

import java.io.InvalidClassException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import darkpanda73.PandaUtils.Utilities.Java.Objects.Pair;

public abstract class NMSPersistentDataContainerAbstract {

	protected final Class<?> persistentDataContainerClass;
	protected final Class<?> persistentDataTypeClass;
	protected final Class<?> namespacedKeyClass;

	protected final Constructor<?> namespacedKeyConstructor;

	protected final Method itemMetaGetPersistentDataContainer;
	protected final Method persistentDataContainerGet;
	protected final Method persistentDataContainerHas;
	protected final Method persistentDataContainerSet;

	public NMSPersistentDataContainerAbstract()
			throws ClassNotFoundException, NoSuchMethodException, SecurityException {
		persistentDataContainerClass = Class.forName("org.bukkit.persistence.PersistentDataContainer");
		persistentDataTypeClass = Class.forName("org.bukkit.persistence.PersistentDataType");
		namespacedKeyClass = Class.forName("org.bukkit.NamespacedKey");

		namespacedKeyConstructor = namespacedKeyClass.getConstructor(Plugin.class, String.class);

		itemMetaGetPersistentDataContainer = ItemMeta.class.getMethod("getPersistentDataContainer");
		persistentDataContainerGet = persistentDataContainerClass.getMethod("get", namespacedKeyClass,
				persistentDataTypeClass);
		persistentDataContainerHas = persistentDataContainerClass.getMethod("has", namespacedKeyClass,
				persistentDataTypeClass);
		persistentDataContainerSet = persistentDataContainerClass.getMethod("set", namespacedKeyClass,
				persistentDataTypeClass, Object.class);
	}

	public Object createNamespacedKey(Plugin plugin, String key)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return namespacedKeyConstructor.newInstance(plugin, key);
	}

	public <T> T get(ItemStack item, Plugin plugin, String key, Class<T> valueClass) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		return get(item, createNamespacedKey(plugin, key), valueClass);
	}

	@SuppressWarnings("unchecked")
	public <T> T get(ItemStack item, Object namespacedKey, Class<T> valueClass)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		if (!namespacedKeyClass.isInstance(namespacedKey)) {
			throw new InvalidClassException("NamespacedKey object is not an instance of hte NamespacedKey class");
		}

		ItemMeta im = item.getItemMeta();

		if (has(item, namespacedKey, valueClass)) {
			Object persistentDataContainer = itemMetaGetPersistentDataContainer.invoke(im);

			return (T) persistentDataContainerGet.invoke(persistentDataContainer, namespacedKey,
					getPersistentDataType(valueClass));
		}

		return null;
	}

	public <T> boolean has(ItemStack item, Plugin plugin, String key, Class<T> valueClass)
			throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			InvalidClassException {
		return has(item, createNamespacedKey(plugin, key), valueClass);
	}

	public <T> boolean has(ItemStack item, Object namespacedKey, Class<T> valueClass)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		if (item == null || !item.hasItemMeta()) {
			return false;
		}

		if (!namespacedKeyClass.isInstance(namespacedKey)) {
			throw new InvalidClassException("NamespacedKey object is not an instance of the NamespacedKey class");
		}

		ItemMeta im = item.getItemMeta();

		Object persistentDataContainer = itemMetaGetPersistentDataContainer.invoke(im);

		return (boolean) persistentDataContainerHas.invoke(persistentDataContainer, namespacedKey,
				getPersistentDataType(valueClass));
	}

	public ItemStack setMultiple(ItemStack item, Plugin plugin, Map<String, Object> values) throws Throwable {
		try {
			return setMultiple(item, values.entrySet().stream().map((entry) -> {
				try {
					return Pair.of(createNamespacedKey(plugin, entry.getKey()), entry.getValue());
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
						| InvocationTargetException e) {
					throw new RuntimeException(e);
				}
			}).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
		} catch (RuntimeException e) {
			throw e.getCause();
		}
	}

	public ItemStack setMultiple(ItemStack item, Map<Object, Object> values)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		ItemMeta im = item.getItemMeta();

		Object persistentDataContainer = itemMetaGetPersistentDataContainer.invoke(im);

		for (Entry<Object, Object> entry : values.entrySet()) {
			if (!namespacedKeyClass.isInstance(entry.getKey())) {
				throw new InvalidClassException("NamespacedKey object is not an instance of hte NamespacedKey class");
			}

			persistentDataContainerSet.invoke(persistentDataContainer, entry.getKey(),
					getPersistentDataType(entry.getValue().getClass()), entry.getValue());
		}

		item.setItemMeta(im);

		return item;
	}

	public ItemStack set(ItemStack item, Plugin plugin, String key, Object value) throws InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		return set(item, createNamespacedKey(plugin, key), value);
	}

	public ItemStack set(ItemStack item, Object namespacedKey, Object value)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InvalidClassException {
		if (!namespacedKeyClass.isInstance(namespacedKey)) {
			throw new InvalidClassException("NamespacedKey object is not an instance of hte NamespacedKey class");
		}

		ItemMeta im = item.getItemMeta();

		Object persistentDataContainer = itemMetaGetPersistentDataContainer.invoke(im);

		persistentDataContainerSet.invoke(persistentDataContainer, namespacedKey,
				getPersistentDataType(value.getClass()), value);

		item.setItemMeta(im);

		return item;
	}

	public abstract Object getPersistentDataType(Class<?> valueClass);

}
