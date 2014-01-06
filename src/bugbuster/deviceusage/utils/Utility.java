package bugbuster.deviceusage.utils;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.net.TrafficStats;
import android.os.RemoteException;
import android.util.Log;

public final class Utility {
	private static final String TAG = "Utility";
	
	/**
	 * Get the version string of specified package.
	 * @param context
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static String getVersion(Context context, String packageName)
			throws NameNotFoundException {
        StringBuilder version = new StringBuilder();
        PackageInfo packageInfo = context.getPackageManager()
                .getPackageInfo(packageName, 0);
        String versionName = packageInfo.versionName;
        if (versionName == null || versionName.isEmpty()) {
            version.append(packageInfo.versionCode);
        } else {
            version.append(versionName).append(" (")
                    .append(packageInfo.versionCode).append(")");
        }
        return version.toString();
    }

	/**
	 * Get total bytes received through network for specified package.
	 * @param context
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static long getReceivedBytes(Context context, String packageName)
			throws NameNotFoundException {
		final PackageManager pm = context.getPackageManager();
		ApplicationInfo packageInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
		return TrafficStats.getUidRxBytes(packageInfo.uid);
	}
	
	/**
	 * Get total bytes sent through network for specified package.
	 * @param context
	 * @param packageName
	 * @return
	 * @throws NameNotFoundException
	 */
	public static long getTransmittedBytes(Context context, String packageName)
			throws NameNotFoundException {
		final PackageManager pm = context.getPackageManager();
		ApplicationInfo packageInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
		return TrafficStats.getUidTxBytes(packageInfo.uid);
	}
	
	// http://www-jo.se/f.pfleger/using-aidl
//	public static long getDiskUsage(Context context, final String packageName) {
//		final PackageManager pm = context.getPackageManager();
//		long diskUsage = 0;
//
//		Method getPackageSizeInfo;
//		try {
//			getPackageSizeInfo = pm.getClass().getMethod(
//			    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
//			
//			getPackageSizeInfo.invoke(pm, packageName,
//			    new IPackageStatsObserver.Stub() {
//
//			        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
//			            throws RemoteException {
//			        	//diskUsage = pStats.cacheSize + pStats.codeSize + pStats.dataSize;
//			            diskUsage = pStats.dataSize;
//			        	Log.i(TAG, "PackageStats for " + packageName + ":" + pStats);
//			        }
//			    });
//			
//		} catch (Exception e) {
//			Log.e(TAG, "error:", e);
//		}
//
//		return diskUsage;
//	}
}
