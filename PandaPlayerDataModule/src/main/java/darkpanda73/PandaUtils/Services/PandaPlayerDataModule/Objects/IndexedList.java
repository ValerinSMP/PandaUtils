package darkpanda73.PandaUtils.Services.PandaPlayerDataModule.Objects;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import lombok.Getter;

public class IndexedList<T extends PlayerData> {

	private Object[] entries;
	private int curIndex = 0;
	private @Getter int occupied = 0;

	public IndexedList(int size) {
		entries = new Object[size];
	}

	public int getSize() {
		return entries.length;
	}

	@SuppressWarnings("unchecked")
	public List<T> getEntries() {
		return Arrays.stream(this.entries).filter(Objects::nonNull).map(obj -> (T) obj).collect(Collectors.toList());
	}

	public int add(T t) {
		return performAction(() -> {
			int startIndex = curIndex;

			while (entries[curIndex = (curIndex + 1) % entries.length] != null && startIndex != curIndex)
				;

			if (curIndex == startIndex) {
				return -1;
			}

			entries[curIndex] = t;
			occupied++;
			return curIndex;
		});
	}

	@SuppressWarnings("unchecked")
	public T get(int index) {
		return performAction(() -> {
			if (index < 0 || index >= entries.length) {
				return null;
			}

			return (T) entries[index];
		});
	}

	public int findIndex(UUID uuid) {
		return performAction(() -> {
			for (int i = 0; i < entries.length && entries[i] != null; i++) {
				if (((PlayerData) entries[i]).getUuid().equals(uuid)) {
					return i;
				}
			}
			return -1;
		});
	}

	public void remove(int index) {
		performAction(() -> {
			entries[index] = null;
			occupied--;
		});
	}

	@SuppressWarnings("unchecked")
	public void removeIf(Function<T, Boolean> condition) {
		performAction(() -> {
			for (int i = 0; i < entries.length && entries[i] != null; i++) {
				if (Boolean.TRUE.equals(condition.apply((T) entries[i]))) {
					entries[i] = null;
					occupied--;
				}
			}
		});
	}

	public void clear() {
		performAction(() -> {
			for (int i = 0; i < entries.length; entries[i++] = null)
				;
			occupied = 0;
		});
	}

	private synchronized void performAction(Runnable action) {
		action.run();
	}

	private synchronized <I> I performAction(Supplier<I> action) {
		return action.get();
	}

}
