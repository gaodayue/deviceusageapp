package bugbuster.deviceusage.access;

import java.util.List;

import android.content.Context;
import bugbuster.deviceusage.access.AppStatistics;

public abstract class LocalStoreService {
	
	protected Context context;
	
	public LocalStoreService(Context context) {
		this.context = context;
	}
	
	public abstract void startService();
	
	public abstract boolean putAppStatistics(AppStatistics app);
	
	public abstract List<AppStatistics> listAllAppStatistics();
	
	public abstract void stopService();
}
