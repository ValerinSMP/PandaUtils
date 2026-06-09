package darkpanda73.PandaUtils.Utilities.Java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

import lombok.Setter;

public class AsyncQueue<T> {

	private static enum Action {
		PULL, PUSH, CLEAR
	}

	private java.util.Queue<T> queue = new LinkedBlockingQueue<T>();
	private List<FailableConsumer<T>> subscriptions = new ArrayList<>();
	private Thread thread;
	private @Setter boolean printErrors = false;

	private T perform(Action action) {
		return perform(action, null);
	}

	private synchronized T perform(Action action, T object) {
		switch (action) {
		case PULL:
			return this.queue.poll();
		case CLEAR:
			this.queue.clear();
			return null;
		case PUSH:
			queue.add(object);

			if (thread == null) {
				thread = new Thread(this::process);
				thread.start();
			}

			return null;
		default:
			return null;
		}
	}

	public void clear() {
		this.perform(Action.CLEAR);
		this.subscriptions.clear();
	}

	public void add(T object) {
		this.perform(Action.PUSH, object);
	}

	public int size() {
		return this.queue.size();
	}

	public void subscribe(FailableConsumer<T> subscription) {
		this.subscriptions.add(subscription);
	}

	public void join() throws InterruptedException {
		if (this.thread != null) {
			this.thread.join();
		}
	}

	public void stop() {
		if (this.thread != null) {
			this.thread.interrupt();
			try {
				this.thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		this.thread = null;
	}

	private void process() {
		try {
			while (this.thread != null && !this.thread.isInterrupted()) {
				T object = perform(Action.PULL);
				if (object != null) {
					new ArrayList<>(this.subscriptions).forEach(subscription -> {
						try {
							subscription.accept(object);
						} catch (Throwable e) {
							if (e instanceof RuntimeException) {
								throw (RuntimeException) e;
							}
							throw new RuntimeException(e);
						}
					});
				} else {
					break;
				}
			}
		} catch (RuntimeException e) {
			if (printErrors) {
				e.printStackTrace();
			}
		}
		this.thread = null;
		return;
	}

	public static interface FailableFunction<T, R> {

		public R apply(T object) throws Throwable;

	}

	public static interface FailableConsumer<T> {

		public void accept(T object) throws Throwable;

	}

	public static interface FailableRunnable {

		public void run() throws Throwable;

	}

	public static interface FailableSupplier<T> {

		public T get() throws Throwable;

	}

}
