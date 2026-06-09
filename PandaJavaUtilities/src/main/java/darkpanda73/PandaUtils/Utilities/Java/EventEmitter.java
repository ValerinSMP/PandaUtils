package darkpanda73.PandaUtils.Utilities.Java;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventEmitter<T> {

	private List<Consumer<T>> subscriptions = new ArrayList<>();

	public void emit(T value) {
		this.subscriptions.forEach(subscription -> subscription.accept(value));
	}

	public void subscribe(Consumer<T> subscription) {
		this.subscriptions.add(subscription);
	}

}
