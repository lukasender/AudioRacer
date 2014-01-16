package at.fhv.audioracer.camera;

import at.fhv.audioracer.core.util.IListener;

public interface OpenCVCameraListener extends IListener {
	public abstract void onNewFrame();
}
