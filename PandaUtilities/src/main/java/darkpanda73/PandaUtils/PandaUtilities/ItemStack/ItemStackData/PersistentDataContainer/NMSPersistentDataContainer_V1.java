package darkpanda73.PandaUtils.PandaUtilities.ItemStack.ItemStackData.PersistentDataContainer;

public class NMSPersistentDataContainer_V1 extends NMSPersistentDataContainerAbstract {

	private final Object persistentDataTypeByte;
	private final Object persistentDataTypeShort;
	private final Object persistentDataTypeInteger;
	private final Object persistentDataTypeLong;
	private final Object persistentDataTypeFloat;
	private final Object persistentDataTypeDouble;
	private final Object persistentDataTypeString;

	public NMSPersistentDataContainer_V1() throws ClassNotFoundException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, IllegalAccessException, NoSuchFieldException {
		super();

		persistentDataTypeByte = persistentDataTypeClass.getField("BYTE").get(persistentDataTypeClass);
		persistentDataTypeShort = persistentDataTypeClass.getField("SHORT").get(persistentDataTypeClass);
		persistentDataTypeInteger = persistentDataTypeClass.getField("INTEGER").get(persistentDataTypeClass);
		persistentDataTypeLong = persistentDataTypeClass.getField("LONG").get(persistentDataTypeClass);
		persistentDataTypeFloat = persistentDataTypeClass.getField("FLOAT").get(persistentDataTypeClass);
		persistentDataTypeDouble = persistentDataTypeClass.getField("DOUBLE").get(persistentDataTypeClass);
		persistentDataTypeString = persistentDataTypeClass.getField("STRING").get(persistentDataTypeClass);
	}

	@Override
	public Object getPersistentDataType(Class<?> valueClass) {
		if (valueClass == Byte.class) {
			return persistentDataTypeByte;
		} else if (valueClass == Short.class) {
			return persistentDataTypeShort;
		} else if (valueClass == Integer.class) {
			return persistentDataTypeInteger;
		} else if (valueClass == Long.class) {
			return persistentDataTypeLong;
		} else if (valueClass == Float.class) {
			return persistentDataTypeFloat;
		} else if (valueClass == Double.class) {
			return persistentDataTypeDouble;
		} else if (valueClass == String.class) {
			return persistentDataTypeString;
		} else {
			return persistentDataTypeString;
		}
	}

}
