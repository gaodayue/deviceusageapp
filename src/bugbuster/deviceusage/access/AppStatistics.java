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
	private long	sendBytes;
	private long	receiveBytes;
	private long	diskUsage;
	
	public AppStatistics(String packageName, String version,
			int foregroundTime, int foregroundCount, int backgroundTime,
			int backgroundCount, long sendBytes, long receiveBytes,
			long diskUsage) {
		this.packageName = packageName;
		this.version = version;
		this.foregroundTime = foregroundTime;
		this.foregroundCount = foregroundCount;
		this.backgroundTime = backgroundTime;
		this.backgroundCount = backgroundCount;
		this.sendBytes = sendBytes;
		this.receiveBytes = receiveBytes;
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
		if (!stats.getPackageName().equals(this.getPackageName())) {
			throw new IllegalArgumentException("cannot update AppStatistics " + this + " with " + stats);
		}
		return new AppStatistics(
				stats.getPackageName(),
				stats.getVersion(),
				stats.getForegroundTime() + foregroundTime,
				stats.getForegroundCount() + foregroundCount,
				stats.getBackgroundTime() + backgroundTime,
				stats.getBackgroundCount() + backgroundCount,
				Math.max(stats.getSendBytes(), sendBytes),
				Math.max(stats.getReceiveBytes(), receiveBytes),
				Math.max(stats.getDiskUsage(), diskUsage));
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

	public long getSendBytes() {
		return sendBytes;
	}

	public long getReceiveBytes() {
		return receiveBytes;
	}

	public long getDiskUsage() {
		return diskUsage;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public String toString() {
		return String.format("{%s/%s: fgtime %d sec, fgcount %d, bgtime %d sec, bgcount %d, tx %d, rx %d, diskusage %d}",
				this.packageName,
				this.version,
				this.foregroundTime,
				this.foregroundCount,
				this.backgroundTime,
				this.backgroundCount,
				this.sendBytes,
				this.receiveBytes,
				this.diskUsage);
	}
}
