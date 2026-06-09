package darkpanda73.PandaUtils.Utilities.Java.Arrays;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ArrayUtilities {

	public static <T> T[] fill(T[] array, T object) {
		Arrays.fill(array, object);
		return array;
	}

	@SafeVarargs
	public static <T> T[] joinDynamic(T[]... arraysToJoin) {
		T[] newArray = Arrays.copyOf(arraysToJoin[0],
				Arrays.asList(arraysToJoin).stream().collect(Collectors.summingInt(array -> array.length)));

		int curIndex = arraysToJoin[0].length;
		for (int arrayIndex = 1; arrayIndex < arraysToJoin.length; arrayIndex++) {
			T[] arrayToJoin = arraysToJoin[arrayIndex];
			for (int i = 0; i < arrayToJoin.length; i++) {
				newArray[curIndex++] = arrayToJoin[i];
				if (curIndex > newArray.length) {
					return newArray;
				}
			}
		}

		return newArray;
	}

	@SafeVarargs
	public static byte[] join(byte[] newArray, byte[]... arraysToJoin) {
		int curIndex = 0;
		for (byte[] arrayToJoin : arraysToJoin) {
			for (int i = 0; i < arrayToJoin.length; i++) {
				newArray[curIndex++] = arrayToJoin[i];
				if (curIndex > newArray.length) {
					return newArray;
				}
			}
		}
		return newArray;
	}

	@SafeVarargs
	public static <T> T[] join(T[] newArray, T[]... arraysToJoin) {
		int curIndex = 0;
		for (T[] arrayToJoin : arraysToJoin) {
			for (int i = 0; i < arrayToJoin.length; i++) {
				newArray[curIndex++] = arrayToJoin[i];
				if (curIndex > newArray.length) {
					return newArray;
				}
			}
		}
		return newArray;
	}

}
