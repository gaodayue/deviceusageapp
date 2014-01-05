package bugbuster.deviceusage.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.util.Log;
import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.access.GoogleAnalyticsUploader;
import bugbuster.deviceusage.access.LocalStoreService;
import bugbuster.deviceusage.access.SQLiteLocalStore;
import bugbuster.deviceusage.access.UploaderService;
import bugbuster.deviceusage.utils.AsycExecutor;
import bugbuster.deviceusage.watcher.AppEventListener;

public class AppEventCallback implements AppEventListener {
	private final String TAG = getClass().getSimpleName();
	
	private AppTracker lastFgTracker;
	private Map<String, AppTracker> fgTrackers;
	private Map<String, AppTracker> bgTrackers;
	
	private LocalStoreService localStore;
	private UploaderService uploader;
	
	// long time operation such as database operation should be
	// done asynchronously in worker thread.
	private AsycExecutor workerThread;
	
	public AppEventCallback(Context context, AsycExecutor workerThread) {
		fgTrackers = new HashMap<String, AppTracker>();
		bgTrackers = new HashMap<String, AppTracker>();
		localStore = new SQLiteLocalStore(context);
		uploader = new GoogleAnalyticsUploader();
		
		this.workerThread = workerThread;
	}
	
	
	@Override
	public void onActivityStart(String pkgName) {
		if (lastFgTracker != null) {
			lastFgTracker.stopTrack();
		}
		
		AppTracker tracker = fgTrackers.get(pkgName);
		if (tracker == null) {
			tracker = new AppForegroundTracker(pkgName);
			tracker.beginSession();
			fgTrackers.put(pkgName, tracker);
		}
		tracker.startTrack();
		lastFgTracker = tracker;
	}

	@Override
	public void onServiceStart(String pkgName) {
		AppTracker tracker = bgTrackers.get(pkgName);
		if (tracker == null) {
			tracker = new AppBackgroundTracker(pkgName);
			tracker.beginSession();
			bgTrackers.put(pkgName, tracker);
		}
		tracker.startTrack();
	}

	@Override
	public void onServiceStop(String pkgName) {
		AppTracker tracker = bgTrackers.get(pkgName);
		if (tracker == null) {
			Log.e(TAG, "onServiceStop: unknow service " + pkgName);
			return;
		}
		tracker.stopTrack();
	}

	@Override
	public void onScreenOff() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void tick() { 
		//----------------------------------------------------
		// store usage data collected in current session
		//----------------------------------------------------
		List<AppTracker> allTrackers = new ArrayList<AppTracker>();
		allTrackers.addAll(fgTrackers.values());
		allTrackers.addAll(bgTrackers.values());
		
		workerThread.asyncRun(new Runnable() {
			public void run() {
				localStore.startService();
			}
		});
		
		for (AppTracker tracker : allTrackers) {
			final AppStatistics data;
			
			if (!tracker.hasData()) {
				continue; // happen when the app never runs in this session
			}
			
			if (tracker.isRunning()) {
				tracker.stopTrack();
				data = tracker.getResult();
				tracker.endSession();
				tracker.beginSession();
				tracker.startTrack();
				
			} else {
				data = tracker.getResult();
				tracker.endSession();
				tracker.beginSession();
			}
			
			workerThread.asyncRun(new Runnable() {
				public void run() {
					Log.v(TAG, "put into database:" + data);
					localStore.putAppStatistics(data);
				}
			});
		}
		
		workerThread.asyncRun(new Runnable() {
			public void run() {
				localStore.stopService();
			}
		});
	}

}
