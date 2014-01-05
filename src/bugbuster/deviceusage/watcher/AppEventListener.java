package bugbuster.deviceusage.watcher;

public interface AppEventListener {
	
	//----------------------------------------------------
	// Implementation of these callback method should finish
	// quickly, any long time operation should be done in a
	// separate thread.
	//----------------------------------------------------
	
	public void onActivityStart(String pkgName);
	
	public void onServiceStart(String pkgName);
	
	public void onServiceStop(String pkgName);

	public void onScreenOff();
	
	public void tick();
}
