package darkpanda73.PandaUtils.Utilities.Java;

import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableRunnable;
import darkpanda73.PandaUtils.Utilities.Java.AsyncQueue.FailableSupplier;

public class SleepingLoop extends Thread {

	private FailableRunnable runnable;
	private FailableSupplier<Long> timeToWaitSupplier;

	private Thread sleepThread;
	private boolean interrupted;

	public SleepingLoop(FailableRunnable runnable, FailableSupplier<Long> timeToWaitSupplier) {
		super();
		this.runnable = runnable;
		this.timeToWaitSupplier = timeToWaitSupplier;
	}

	public void run() {
		while (!this.interrupted) {
			try {
				runnable.run();
			} catch (InterruptedException e) {
				continue;
			} catch (Throwable e) {
				e.printStackTrace();
				break;
			}

			try {
				sleepThread = new Thread(() -> {
					try {
						Thread.sleep(timeToWaitSupplier.get());
					} catch (InterruptedException e) {
					} catch (Throwable e) {
						e.printStackTrace();
					}
				});

				sleepThread.start();
				sleepThread.join();
			} catch (InterruptedException e) {
			}
		}
	}

	public void awake() {
		if (this.sleepThread != null) {
			this.sleepThread.interrupt();
		}
	}

	@Override
	public void interrupt() {
		this.interrupted = true;
		super.interrupt();
	}

}
