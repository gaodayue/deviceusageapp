package bugbuster.deviceusage.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;
import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.access.LocalStoreService;
import bugbuster.deviceusage.access.SQLiteLocalStore;
import bugbuster.deviceusage.utils.AsycExecutor;
import bugbuster.deviceusage.utils.Utility;
import bugbuster.deviceusage.watcher.AppEventListener;

public class AppEventCallback implements AppEventListener {
	private final String TAG = getClass().getSimpleName();
	
	private Context context;
	
	private AppTracker lastFgTracker;
	private Map<String, AppTracker> fgTrackers;
	private Map<String, AppTracker> bgTrackers;
	
	private LocalStoreService localStore;
	
	// long time operation such as database operation should be
	// done asynchronously in worker thread.
	private AsycExecutor workerThread;
	
	public AppEventCallback(Context context, AsycExecutor workerThread) {
		this.context = context;
		
		fgTrackers = new HashMap<String, AppTracker>();
		bgTrackers = new HashMap<String, AppTracker>();
		localStore = new SQLiteLocalStore(context);
		
		this.workerThread = workerThread;
	}
	
	
	@Override
	public void onActivityStart(String pkgName) {
		String version = null;
		try {
			version = Utility.getVersion(context, pkgName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "error:", e);
			return;
		}
		
		if (lastFgTracker != null) {
			lastFgTracker.stopTrack();
		}
		
		AppTracker tracker = fgTrackers.get(pkgName);
		if (tracker == null) {
			tracker = new AppForegroundTracker(pkgName, version);
			tracker.beginSession();
			fgTrackers.put(pkgName, tracker);
		}
		tracker.startTrack();
		lastFgTracker = tracker;
	}

	@Override
	public void onServiceStart(String pkgName) {
		String version = null;
		try {
			version = Utility.getVersion(context, pkgName);
		} catch (NameNotFoundException e) {
			Log.e(TAG, "error:", e);
			return;
		}
		
		AppTracker tracker = bgTrackers.get(pkgName);
		if (tracker == null) {
			tracker = new AppBackgroundTracker(pkgName, version);
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
		final List<AppTracker> allTrackers = new ArrayList<AppTracker>();
		allTrackers.addAll(fgTrackers.values());
		allTrackers.addAll(bgTrackers.values());
		
		workerThread.asyncRun(new Runnable() {
			public void run() {
				localStore.startService();
				
				try {
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
						
						Log.v(TAG, "put into database:" + data);
						localStore.putAppStatistics(data);
					}
					
				} catch (Exception e) {
					Log.e(TAG, "error:", e);
				} finally {
					localStore.stopService();
				}
			}
		});
	}

}
