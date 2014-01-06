package bugbuster.deviceusage.access;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.content.Context;
import android.util.Log;
import bugbuster.deviceusage.access.AppStatistics;

public class GoogleAnalyticsUploader extends UploaderService {

	private EasyTracker easyTracker;

	public String TAG = "ANALYTICS";

	public GoogleAnalyticsUploader(Context context) {
		super(context);
	}

	@Override
	public void startService() {
		easyTracker = EasyTracker.getInstance(this.context);
		//GoogleAnalytics.getInstance(context).getLogger().setLogLevel(LogLevel.VERBOSE);
		easyTracker.send(MapBuilder.createAppView().build());
	}

	private void createEvent(String categoy, String action, String label,
			Long value) {

		// MapBuilder.createEvent().build() returns a Map of event fields and
		// values that are set and sent with the hit.

		easyTracker.send(MapBuilder.createEvent(categoy, action, label, value)
				.build());
		Log.v(TAG, "Send an event");
	}

	@Override
	public void uploadAppStatistics(AppStatistics app) {
		createEvent("ForegroundTime", app.getPackageName(), app.getVersion(),
				(long) app.getForegroundTime());
		createEvent("BackgroundTime", app.getPackageName(), app.getVersion(),
				(long) app.getBackgroundTime());
		createEvent("ForegroundCount", app.getPackageName(), app.getVersion(),
				(long) app.getForegroundCount());
		createEvent("BackgroundCount", app.getPackageName(), app.getVersion(),
				(long) app.getBackgroundCount());
		createEvent("SendBytes", app.getPackageName(), app.getVersion(), app.getSendBytes());
		createEvent("ReceiveBytes", app.getPackageName(), app.getVersion(), app.getReceiveBytes());
		createEvent("DiskUsage", app.getPackageName(), app.getVersion(), app.getDiskUsage());
	}

	@Override
	public void stopService() {

	}

}
