package darkpanda73.PandaUtils.Utilities.Java.Objects;

import java.util.function.Function;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class ReferencedObject<T> {

	private @Getter String identifier;
	private Function<String, T> provider;

	public T getObject() {
		return provider.apply(identifier);
	}

}
