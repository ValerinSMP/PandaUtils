package darkpanda73.PandaUtils.Utilities.Java.Collections;

import java.util.function.Consumer;

public class RotativeArray<T> {

	private T[] array;
	private int index = 0;

	@SafeVarargs
	public RotativeArray(T... originalArray) {
		this.array = originalArray;
	}

	public T get() {
		return this.array[index];
	}

	public T previous() {
		return this.array[index = (array.length + index - 1) % array.length];
	}

	public T next() {
		return this.array[index = (array.length + index + 1) % array.length];
	}

	public int getIndex() {
		return this.index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int findIndex(T item) {
		for (int i = 0; i < array.length; i++) {
			if (array[i] == item || array[i].equals(item)) {
				return i;
			}
		}
		return -1;
	}

	public void forEach(Consumer<T> consumer) {
		for (int i = 0; i < array.length; i++) {
			try {
				consumer.accept(array[i]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}
