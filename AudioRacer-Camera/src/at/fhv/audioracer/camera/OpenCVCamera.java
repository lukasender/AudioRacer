package at.fhv.audioracer.camera;

import org.opencv.core.Mat;

import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.util.ListenerList;

public class OpenCVCamera {
	private static class OpenCVCameraListenerList extends ListenerList<OpenCVCameraListener>
			implements OpenCVCameraListener {
		
		@Override
		public void onNewFrame() {
			for (OpenCVCameraListener listener : listeners()) {
				listener.onNewFrame();
			}
		}
		
	}
	
	private OpenCVCameraListenerList _listenerList;
	
	public OpenCVCamera() {
		_listenerList = new OpenCVCameraListenerList();
	}
	
	public ListenerList<OpenCVCameraListener> getListenerList() {
		return _listenerList;
	}
	
	public Mat getFrame() {
		return null;
	}
	
	public void setMap(Map map) {
		// from here you are allowed to detect cars.
	}
	
	public void openCamera(int id) {
		
	}
	
	public void beginPositioning() {
		
	}
	
	public void setPosition(int x, int y) {
		
	}
	
	public void setZoom(int zoom) {
		
	}
	
	public void endPositioning() {
		
	}
	
	public void startCalibration() {
		
	}
	
	public void endCalibration() {
		
	}
	
}