package bugbuster.deviceusage.component;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class DeviceUsageService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
