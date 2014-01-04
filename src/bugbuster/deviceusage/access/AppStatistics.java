package bugbuster.deviceusage.access;

import android.annotation.SuppressLint;

/**
 * An immutable object represents usage statistics for one App.
 * @author gaod1
 */
public class AppStatistics {
	private String	packageName;
	private String	version;
	private int		foregroundTime;
	private int		foregroundCount;
	private int		backgroundTime;
	private int		backgroundCount;
	private int		networkTracfics;
	private int		diskUsage;
	
	public AppStatistics(String packageName, String version,
			int foregroundTime, int foregroundCount, int backgroundTime,
			int backgroundCount, int networkTracfics, int diskUsage) {
		this.packageName = packageName;
		this.version = version;
		this.foregroundTime = foregroundTime;
		this.foregroundCount = foregroundCount;
		this.backgroundTime = backgroundTime;
		this.backgroundCount = backgroundCount;
		this.networkTracfics = networkTracfics;
		this.diskUsage = diskUsage;
	}
	
	/**
	 * Update current statistics using data from `stats`.
	 * 
	 * @param stats
	 * @return Since AppStatistics is immutable, return another object representing
	 *         the updated status.
	 */
	public AppStatistics update(AppStatistics stats) {
		if (stats.getPackageName().equals(this.getPackageName())) {
			throw new IllegalArgumentException("cannot update AppStatistics " + this + " with " + stats);
		}
		return new AppStatistics(
				stats.getPackageName(),
				stats.getVersion(),
				stats.getForegroundTime() + foregroundTime,
				stats.getForegroundCount() + foregroundCount,
				stats.getBackgroundTime() + backgroundTime,
				stats.getBackgroundCount() + backgroundCount,
				stats.getNetworkTracfics() + networkTracfics,
				stats.getDiskUsage() + diskUsage);
	}

	public String getPackageName() {
		return packageName;
	}

	public String getVersion() {
		return version;
	}

	public int getForegroundTime() {
		return foregroundTime;
	}

	public int getForegroundCount() {
		return foregroundCount;
	}

	public int getBackgroundTime() {
		return backgroundTime;
	}

	public int getBackgroundCount() {
		return backgroundCount;
	}

	public int getNetworkTracfics() {
		return networkTracfics;
	}

	public int getDiskUsage() {
		return diskUsage;
	}
	
	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("{%s/%s: fgtime %d sec, fgcount %d, bgtime %d sec, bgcount %d}",
				this.packageName,
				this.version,
				this.foregroundTime,
				this.foregroundCount,
				this.backgroundTime,
				this.backgroundCount);
	}
}
