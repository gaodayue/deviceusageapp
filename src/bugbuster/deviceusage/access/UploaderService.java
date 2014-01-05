package bugbuster.deviceusage.access;

import android.content.Context;
import bugbuster.deviceusage.access.AppStatistics;

public abstract class UploaderService {
	
	protected Context context;
	
	public UploaderService(Context context) {
		this.context = context;
	}
	
	public abstract void startService();
	
	public abstract void uploadAppStatistics(AppStatistics app);
	
	public abstract void stopService();
}
