package at.fhv.audioracer.client.android.activity.thread.util;

import at.fhv.audioracer.client.android.activity.PlayGameActivity.ControlMode;
import at.fhv.audioracer.client.android.activity.thread.ControlThread;

public class ThreadControlMode {
	public ControlMode mode;
	public ControlThread thread;
	
	public ThreadControlMode(ControlMode mode, ControlThread thread) {
		this.mode = mode;
		this.thread = thread;
	}
}