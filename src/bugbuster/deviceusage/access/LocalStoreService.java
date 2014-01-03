package bugbuster.deviceusage.access;

import java.util.List;

import bugbuster.deviceusage.component.TrackedApp;

public interface LocalStoreService {
	
	public void startService();
	
	public boolean putTrackedApp(TrackedApp app);
	
	public List<TrackedApp> listAllTrackedApps();
	
	public void stopService();
}
