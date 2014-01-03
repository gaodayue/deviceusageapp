package bugbuster.deviceusage.component;

public class TrackedApp {
	private String packageName;
	private String version;
	private long fgTime;
	private long bgTime;
	private int fgCount;
	private int bgCount;
	
	private int cpuUsage;
	private int uss; // unique memory size; pages unique to a process in kB
	private int pss; // memory shared size in kB
	private long rxBytes; // number of bytes received
	private long txBytes; // number of bytes transmitted
}
