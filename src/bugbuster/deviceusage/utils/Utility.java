package bugbuster.deviceusage.utils;

import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.net.TrafficStats;
import android.os.RemoteException;

public final class Utility {

	private Utility(){
	}
	
	public static long getReceivedBytes(Context context, String packageName){
		final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        int uid = -1;
        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)){
                //get the UID for the selected app
                uid = packageInfo.uid;
            }
        }
        long rx = TrafficStats.getUidRxBytes(uid);
        if (rx == -1) {
        	return -1L; // data not found, or unsupported
        }
        return rx;
	}
	
	public static long getTransmittedBytes(Context context, String packageName){
		final PackageManager pm = context.getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        int uid = -1;
        //loop through the list of installed packages and see if the selected
        //app is in the list
        for (ApplicationInfo packageInfo : packages) {
            if(packageInfo.packageName.equals(packageName)){
                //get the UID for the selected app
                uid = packageInfo.uid;
            }
        }
        long tx = TrafficStats.getUidTxBytes(uid);
        if (tx == -1) {
        	return -1L; // data not found, or unsupported
        }
        return tx;
	}
	
	// http://www-jo.se/f.pfleger/using-aidl
	public static long getDiskUsage(Context context, String packageName){
		final PackageManager pm = context.getPackageManager();
		long diskUsage = 0;

		Method getPackageSizeInfo;
		try {
			getPackageSizeInfo = pm.getClass().getMethod(
			    "getPackageSizeInfo", String.class, IPackageStatsObserver.class);
			
			getPackageSizeInfo.invoke(pm, packageName,
			    new IPackageStatsObserver.Stub() {

			        public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
			            throws RemoteException {
			        	//diskUsage = pStats.cacheSize + pStats.codeSize + pStats.dataSize;
			            diskUsage = pStats.cacheSize;
			        	//Log.i(TAG, "codeSize: " + pStats.codeSize);
			        }
			    });
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return diskUsage;
	}
}
