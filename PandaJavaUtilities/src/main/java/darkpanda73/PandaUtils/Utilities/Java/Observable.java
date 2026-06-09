package darkpanda73.PandaUtils.Utilities.Java;

import java.util.function.Consumer;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Observable<T> {

	private FallibleSupplier<T> supplier;

	public void subscribe() {
		subscribe(null);
	}

	public void subscribe(Consumer<T> onSuccess) {
		subscribe(onSuccess, null);
	}

	public void subscribe(Consumer<T> onSuccess, Consumer<Throwable> onError) {
		T result;

		try {
			result = supplier.get();
		} catch (Throwable e) {
			if (onError != null) {
				onError.accept(e);
			}
			return;
		}

		if (onSuccess != null) {
			onSuccess.accept(result);
		}
	}

	public static <T> Observable<T> of(FallibleSupplier<T> supplier) {
		return new Observable<>(supplier);
	}

	public static interface FallibleSupplier<T> {

		public T get() throws Throwable;

	}

}
