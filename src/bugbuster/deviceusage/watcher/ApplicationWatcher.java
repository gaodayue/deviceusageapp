package bugbuster.deviceusage.watcher;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.access.GoogleAnalyticsUploader;
import bugbuster.deviceusage.utils.AsycExecutor;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PowerManager;
import android.util.Log;

public class ApplicationWatcher {
	private final String TAG = "ApplicationWatcher";
	
	class Watcher implements Runnable {
		private static final int MAX_SERVICES_NUM = 50;
		
		private ActivityManager am;
		private PowerManager pm;
		private KeyguardManager km;
		
		private String lastFgApp;
		private Set<String> lastServices;
		
		public Watcher() {
			am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
			
			lastServices = new HashSet<String>();
			
		
		}
		
		@Override
		public void run() {
			if (isWatch.get()) {
				Log.d(TAG, "in watcher#run()");
				
				//----------------------------------------------------
				// get foreground activity information
				//----------------------------------------------------
				if (pm.isScreenOn() && !km.isKeyguardLocked()) {
					final String currentFgApp = getForegroundActivity();
					
					if (null != currentFgApp && !currentFgApp.equals(lastFgApp)) {
						Log.d(TAG, "found a new fg app: " + currentFgApp);
						lastFgApp = currentFgApp;
						listernerThread.asyncRun(new Runnable() {
							
							@Override
							public void run() {
								listener.onActivityStart(currentFgApp);
							}
						});
					}
				}
				
				//----------------------------------------------------
				// get foreground services, such as music player service
				//----------------------------------------------------
				final Set<String> currentFgServices = getForegroundServices();
				final Set<String> servicesStarted = setDifference(currentFgServices, lastServices);
				final Set<String> servicesStoped  = setDifference(lastServices, currentFgServices);
				lastServices.removeAll(servicesStoped);
				lastServices.addAll(servicesStarted);
				
				listernerThread.asyncRun(new Runnable() {
					
					@Override
					public void run() {
						for (String pkgName : servicesStarted) {
							Log.d(TAG, "found newly started foreground service: " + pkgName);
							listener.onServiceStart(pkgName);
						}
						
						for (String pkgName : servicesStoped) {
							Log.d(TAG, "found newly stoped foreground service: " + pkgName);
							listener.onServiceStop(pkgName);
						}
					}
				});
				
				
				watcherHandler.postDelayed(this, 1000);
			}
		}
		
		/**
		 * Get the package name of current foreground activity.
		 * @return package name of the current foreground application.
		 *         null if no one exists.
		 */
		private String getForegroundActivity() {
			List<RunningTaskInfo> tasks = am.getRunningTasks(/* maxNum= */1);
			if (tasks.size() < 1)
				return null;
			
			RunningTaskInfo task = tasks.get(0);
			ComponentName activityName = task.baseActivity;
			return activityName.getPackageName();
		}
		
		/**
		 * Get set of package names for running foreground services. 
		 * @return a set containing package name for each app's foreground services.
		 */
		private Set<String> getForegroundServices() {
			Set<String> res = new HashSet<String>();
			
			List<RunningServiceInfo> runningServices = am.getRunningServices(MAX_SERVICES_NUM);
			for (RunningServiceInfo serviceInfo : runningServices) {
				if (serviceInfo.foreground) {
					res.add(serviceInfo.service.getPackageName());
				}
			}
			
			return res;
		}
		
		/**
		 * Return difference of set `s1` and set `s2`.
		 * @param s1
		 * @param s2
		 * @return
		 */
		private Set<String> setDifference(Set<String> s1, Set<String> s2) {
			Set<String> diff = new HashSet<String>(s1);
			diff.removeAll(s2);
			return diff;
		}
	}
	
	private Context context;
	
	private HandlerThread watcherThread;
	private Handler watcherHandler;
	private Watcher watcher;
	
	// callback methods in listener are executed in the listener thread
	private AsycExecutor listernerThread;
	private AppEventListener listener;
	
	private AtomicBoolean isWatch = new AtomicBoolean(false);
	
	public ApplicationWatcher(Context context, AppEventListener listener) {
		this.context = context;
		
		watcherThread = new HandlerThread("watcher thread");
		watcherThread.start();
		
		// make watcherHandler to send message to watcher thread's message queue
		watcherHandler = new Handler(watcherThread.getLooper());
		watcher = new Watcher();
		
		this.listernerThread = new AsycExecutor();
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
		this.watcherThread.quit();
		this.listernerThread.quit();
	}
}
