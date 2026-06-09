package darkpanda73.PandaUtils.PandaThreads;

import java.util.ArrayList;
import java.util.List;

import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService;
import darkpanda73.PandaUtils.PandaPlugin.Annotations.PandaService.UnloadMethod;
import darkpanda73.PandaUtils.PandaPlugin.Enums.LoadStep;
import darkpanda73.PandaUtils.PandaThreads.Objects.Thread;

@PandaService(loadOn = LoadStep.LOAD, priority = -999)
public class PandaThreadsService {

	private List<Thread> threads = new ArrayList<>();

	@UnloadMethod
	private void unload() {
		unregisterThreads();
	}

	public Thread registerThread(Thread.Runnable runnable) {
		return this.registerThread(runnable, null);
	}

	public Thread registerThread(Thread.Runnable runnable, Thread.CallBack callback) {
		return this.registerThread(new Thread(runnable, callback));
	}

	public Thread registerThread(Thread thread) {
		return threads.add(thread) ? thread : null;
	}

	public void unregisterThread(Thread thread) {
		this.unregisterThread(thread.getId());
	}

	public void unregisterThread(long threadid) {
		for (Thread thread : new ArrayList<>(threads)) {
			if (thread.getId() == threadid) {
				try {
					thread.stop();
				} catch (Exception e) {
				}
				threads.remove(thread);
				break;
			}
		}
	}

	public void unregisterThreads() {
		for (Thread thread : new ArrayList<>(threads))
			try {
				thread.stop();
			} catch (Exception e) {
			}
		threads.clear();
	}

}
