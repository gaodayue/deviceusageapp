package bugbuster.deviceusage.watcher;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

public class ApplicationWatcher {
	private final String TAG = "ApplicationWatcher";
	
	class Watcher implements Runnable {
		
		@Override
		public void run() {
			if (isWatch.get()) {
				
				Log.d(TAG, "in watcher#run()");
				
				watcherHandler.postDelayed(this, 1000);
			}
		}
		
	}
	
	private HandlerThread watcherThread;
	private Handler watcherHandler;
	private Watcher watcher;
	private AppEventListener listener;
	
	private AtomicBoolean isWatch = new AtomicBoolean(false);
	
	public ApplicationWatcher(AppEventListener listener) {
		watcherThread = new HandlerThread("watcher thread");
		watcherThread.start();
		
		// make watcherHandler to send message to watcher thread's message queue
		watcherHandler = new Handler(watcherThread.getLooper());
		watcher = new Watcher();
		this.listener = listener;
	}
	
	/**
	 * Start the application watcher to collect usage data in background.
	 * 
	 * Do nothing if it has been started.
	 */
	public void start() {
		if (!isWatch.get()) {
			Log.d(TAG, "watcher started!");
			isWatch.set(true);
			
			watcherHandler.post(watcher);	
		}
	}

	/**
	 * Stop the application watcher, which then 
	 * can be restarted by calling start().
	 * 
	 * Note that this just stop collecting data,
	 * the background watcher thread is still alive.
	 */
	public void stop() {
		if (isWatch.get()) {
			Log.d(TAG, "watcher stopped!");
			isWatch.set(false);
			
			watcherHandler.removeCallbacks(watcher);
		}
	}
	
	/**
	 * Shutdown the application watcher.
	 * 
	 * This will stop the background watcher thread and
	 * no operation should be performed later.
	 */
	public void shutdown() {
		this.stop();
		watcherThread.quit();
	}
}
