package darkpanda73.PandaUtils.Utilities.Java.Objects;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair<T, S> {

	private T first;
	private S second;

	public T getKey() {
		return this.first;
	}

	public S getValue() {
		return this.second;
	}

	public static <T, S> Pair<T, S> of(T first, S second) {
		return new Pair<>(first, second);
	}

}
