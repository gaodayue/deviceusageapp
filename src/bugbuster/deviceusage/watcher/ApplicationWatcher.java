package bugbuster.deviceusage.watcher;

import java.util.concurrent.atomic.AtomicBoolean;

import android.os.Handler;
import android.os.HandlerThread;

public class ApplicationWatcher {
	
	class Watcher implements Runnable {
		
		private AtomicBoolean isWatch;
		
		public Watcher() {
			isWatch = new AtomicBoolean(false);
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			watcherHandler.postDelayed(this, 1000);
		}
		
	}
	
	private HandlerThread watcherThread;
	private Handler watcherHandler;
	private Watcher watcher;
	private AppEventListener listener;
	
	public ApplicationWatcher(AppEventListener listener) {
		watcherThread = new HandlerThread("watcher thread");
		watcherThread.start();
		
		// make watcherHandler to send message to watcher thread's message queue
		watcherHandler = new Handler(watcherThread.getLooper());
		watcher = new Watcher();
		this.listener = listener;
	}
	
	public void start() {
		if (!watcher.isWatch.get()) {
			watcher.isWatch.set(true);
			watcherHandler.post(watcher);	
		}
	}

	public void stop() {
		if (watcher.isWatch.get()) {
			watcher.isWatch.set(false);
			watcherHandler.removeCallbacks(watcher);
		}
	}
	
	public void shutdown() {
		this.stop();
		watcherThread.quit();
	}
}
