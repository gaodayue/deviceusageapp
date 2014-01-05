package bugbuster.deviceusage.access;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.GoogleAnalytics;
import com.google.analytics.tracking.android.Logger.LogLevel;
import com.google.analytics.tracking.android.MapBuilder;

import android.content.Context;
import android.util.Log;
import bugbuster.deviceusage.access.AppStatistics;
import bugbuster.deviceusage.component.MainActivity;

//Emule call to Google anal - Begin ------------
/*
 bugbuster.deviceusage.access.AppStatistics app;
 app = new AppStatistics("com.fake.thing", "1.42", 128, 3, 127, 4,
 120000, 12);

 bugbuster.deviceusage.access.GoogleAnalyticsUploader ga;
 ga = new GoogleAnalyticsUploader(this);
 ga.startService();
 ga.uploadAppStatistics(app);
 */
// Emule call to Google anal - End ------------

public class GoogleAnalyticsUploader implements UploaderService {

	private Context context;

	private EasyTracker easyTracker;

	public GoogleAnalyticsUploader(Context context) {
		this.context = context;
	}

	public String TAG = "ANALYTICS";

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
		Log.d(TAG, "Send an event");
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
		createEvent("NetworkTraffic", app.getPackageName(), app.getVersion(),
				(long) app.getNetworkTracfics());
		createEvent("DiskUsage", app.getPackageName(), app.getVersion(),
				(long) app.getDiskUsage());
	}

	@Override
	public void stopService() {

	}

}
