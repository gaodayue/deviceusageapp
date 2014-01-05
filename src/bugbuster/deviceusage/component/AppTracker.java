package bugbuster.deviceusage.component;

import bugbuster.deviceusage.access.AppStatistics;
import android.os.SystemClock;

public abstract class AppTracker {
	
	/**
	 * capture the order in which methods should be called.
	 */
	private enum STATE {
		NO_SESSION {
			@Override
			STATE beginSession() {
				return SESSION_START;
			}
		},
		SESSION_START {
			@Override
			STATE startTrack() {
				return STARTED;
			}
		},
		STARTED {
			@Override
			STATE stopTrack() {
				return STOPPED;
			}
		},
		STOPPED {
			@Override
			STATE startTrack() {
				return STARTED;
			}
			@Override
			STATE endSession() {
				return NO_SESSION;
			}
		};
		
		STATE beginSession() { throw new IllegalStateException(this.name()); }
		STATE startTrack() { throw new IllegalStateException(this.name()); }
		STATE stopTrack() { throw new IllegalStateException(this.name()); }
		STATE endSession() { throw new IllegalStateException(this.name()); }
	}
	
	protected STATE state = STATE.NO_SESSION;
	
	protected String packageName;
	protected String version;
	
	protected long totalStartTime;
	protected int totalStartCount;
	
	protected long lastStartTime;
	
	public AppTracker(String trackedApp) {
		this.packageName = trackedApp;
	}
	
	/**
	 * Get the tacking result for the current session.
	 * Should only be called when tracker is in STOPPED state.
	 * 
	 * @return tracking result
	 */
	public AppStatistics getResult() {
		if (state != STATE.STOPPED)
			throw new IllegalStateException("AppTracker in invalid state:" + state.name());
		return constructStatistics();
	}
	
	protected abstract AppStatistics constructStatistics();
	
	public boolean hasData() {
		return state == STATE.STARTED || state == STATE.STOPPED;
	}
	
	public boolean isRunning() {
		return state == STATE.STARTED;
	}
	
	//----------------------------------------------------
	// Methods to control tracker's lifetime, should be
	// called in specific order like:
	//
	// tracker.beginSession();
	// tracker.start();
	// tracker.stop();
	// ...start, stop...
	// tracker.endSession();
	//----------------------------------------------------
	
	public void beginSession() {
		state = state.beginSession();
		this.lastStartTime = 0;
		this.totalStartTime = 0;
		this.totalStartCount = 0;
	}
	
	public void startTrack() {
		state = state.startTrack();
		this.lastStartTime = SystemClock.elapsedRealtime();
		this.totalStartCount++;
	}
	
	public void stopTrack() {
		state = state.stopTrack();
		this.totalStartTime += SystemClock.elapsedRealtime() - this.lastStartTime;
	}
	
	public void endSession() {
		state = state.endSession();
	}
}
