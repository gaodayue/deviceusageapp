package bugbuster.deviceusage.component;

import bugbuster.deviceusage.utils.AsycExecutor;
import bugbuster.deviceusage.watcher.ApplicationWatcher;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DeviceUsageService extends Service {
	private final String TAG = getClass().getSimpleName();
	
	private AsycExecutor executor;
	
	private ApplicationWatcher appWatcher;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate() DeviceUsageService");
		
		this.executor = new AsycExecutor();
		this.appWatcher = new ApplicationWatcher(new AppEventCallback());
		
		// TODO register any receivers here
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() DeviceUsageService");
		
		executor.asyncRun(new Runnable() {
			@Override
			public void run() {
				appWatcher.start();
			}
		});
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy() DeviceUsageService");
		
		// unregister receivers here
		
		executor.asyncRun(new Runnable() {
			@Override
			public void run() {
				appWatcher.shutdown();
			}
		});
		
		executor.shutdown();
	}

}
