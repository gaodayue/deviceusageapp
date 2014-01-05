package bugbuster.deviceusage.component;

import bugbuster.deviceusage.utils.AsycExecutor;
import bugbuster.deviceusage.watcher.ApplicationWatcher;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class DeviceUsageService extends Service {
	private final String TAG = getClass().getSimpleName();
	
	private ApplicationWatcher appWatcher;
	private AppEventCallback eventCallback;
	
	private AsycExecutor callbackExecutor;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate() DeviceUsageService");
		
		this.callbackExecutor = new AsycExecutor();
		this.eventCallback = new AppEventCallback(this, callbackExecutor);
		
		this.appWatcher = new ApplicationWatcher(this, eventCallback);
		
		// TODO register any receivers here
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.d(TAG, "onStartCommand() DeviceUsageService");
		
		appWatcher.start();
		
		return START_STICKY;
	}
	
	@Override
	public void onDestroy() {
		Log.d(TAG, "onDestroy() DeviceUsageService");
		
		// unregister receivers here
		
		appWatcher.shutdown();
		callbackExecutor.quit();
	}

}
