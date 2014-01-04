package bugbuster.deviceusage.access;

import bugbuster.deviceusage.access.AppStatistics;

public interface UploaderService {
	
	public void startService();
	
	public void uploadAppStatistics(AppStatistics app);
	
	public void stopService();
}
