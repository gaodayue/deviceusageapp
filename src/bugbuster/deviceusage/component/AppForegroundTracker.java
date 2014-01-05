package bugbuster.deviceusage.component;

import java.util.concurrent.TimeUnit;

import bugbuster.deviceusage.access.AppStatistics;

public class AppForegroundTracker extends AppTracker {

	public AppForegroundTracker(String trackedApp) {
		super(trackedApp);
	}

	@Override
	protected AppStatistics constructStatistics() {
		return new AppStatistics(
				packageName, version,
				(int) TimeUnit.MILLISECONDS.toSeconds(totalStartTime),
				totalStartCount,
				0, 0, 0, 0, 0);
	}

}
