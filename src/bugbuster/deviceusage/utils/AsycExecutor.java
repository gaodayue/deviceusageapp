package bugbuster.deviceusage.utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public final class AsycExecutor {
	private final String TAG = getClass().getSimpleName();
	
	private final ExecutorService executor;
	
	public AsycExecutor() {
		executor = Executors.newSingleThreadExecutor();
	}
	
	/**
	 * Task will be guaranteed to run in sequence.
	 * @param task
	 */
	public void asyncRun(Runnable task) {
		try {
			executor.execute(task);
		} catch (Exception e) {
			Log.e(TAG, "error:", e);
		}
	}
	
	public void quit() {
		try {
			executor.shutdown();
		} catch (Exception e) {
			Log.e(TAG, "error:", e);
		}
	}
}
