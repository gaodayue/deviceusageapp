package bugbuster.deviceusage.component;

import java.util.concurrent.TimeUnit;

import bugbuster.deviceusage.access.AppStatistics;

public class AppBackgroundTracker extends AppTracker {

	public AppBackgroundTracker(String trackedApp) {
		super(trackedApp);
	}

	@Override
	protected AppStatistics constructStatistics() {
		return new AppStatistics(
				packageName, version, 0, 0,
				(int) TimeUnit.MILLISECONDS.toSeconds(totalStartTime),
				totalStartCount,
				0, 0, 0);
	}

}
