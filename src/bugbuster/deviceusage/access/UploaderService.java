package bugbuster.deviceusage.access;

import bugbuster.deviceusage.component.TrackedApp;

public interface UploaderService {
	
	public void startService();
	
	public void uploadTrackedApp(TrackedApp app);
	
	public void stopService();
}
