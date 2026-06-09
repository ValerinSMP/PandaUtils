package darkpanda73.PandaUtils.Utilities.Java.Enum;

public class EnumUtilities {

	public static <T extends Enum<T>> T findByAny(Class<T> enumClass, String... names) {
		for (int i = 0; i < names.length; i++) {
			try {
				return Enum.valueOf(enumClass, names[i]);
			} catch (IllegalArgumentException e) {
			}
		}
		return null;
	}

}
