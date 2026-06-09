package darkpanda73.PandaUtils.Utilities.Java.Collections;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class ListUtilities {

	public static <T> List<T> filter(List<T> list, Function<T, Boolean> filterFunction) {
		List<T> filteredList = new ArrayList<>();
		T obj;
		for (int i = 0; i < list.size(); i++) {
			if (filterFunction.apply((obj = list.get(i)))) {
				filteredList.add(obj);
			}
		}
		return filteredList;
	}

	public static <T> T findFirst(List<T> list) {
		return !list.isEmpty() ? list.get(0) : null;
	}

}
