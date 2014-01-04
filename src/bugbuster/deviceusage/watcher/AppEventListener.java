package bugbuster.deviceusage.watcher;

public interface AppEventListener {
	
	public void onActivityStart(String name, String version);
	
	public void onServiceStart(String name, String version);
	
	public void onServiceStop(String name, String version);

	public void onScreenOff();
	
	public void tick();
}
