package bugbuster.deviceusage.watcher;

public interface AppEventListener {
	
	public void onActivityStart(String pkgName);
	
	public void onServiceStart(String pkgName);
	
	public void onServiceStop(String pkgName);

	public void onScreenOff();
	
	public void tick();
}
