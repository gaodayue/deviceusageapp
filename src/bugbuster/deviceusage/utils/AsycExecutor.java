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
	
	public void asyncRun(Runnable task) {
		try {
			executor.execute(task);
		} catch (Exception e) {
			Log.e(TAG, "error:", e);
		}
	}
	
	public void shutdown() {
		try {
			executor.shutdown();
		} catch (Exception e) {
			Log.e(TAG, "error:", e);
		}
	}
}
