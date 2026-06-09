package darkpanda73.PandaUtils.Utilities.Java;

import java.util.concurrent.Semaphore;

import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableRunnable;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableSupplier;

public class SyncQueue {

	private Semaphore semaphore = new Semaphore(1, true);

	public <T> T executeWithReturn(FailableSupplier<T> func) throws Throwable {
		semaphore.acquire();
		try {
			return func.get();
		} finally {
			semaphore.release();
		}
	}

	public void execute(FailableRunnable func) throws Throwable {
		semaphore.acquire();
		try {
			func.run();
		} finally {
			semaphore.release();
		}
	}

}
