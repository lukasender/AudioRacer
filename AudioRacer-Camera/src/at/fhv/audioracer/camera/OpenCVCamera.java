package at.fhv.audioracer.camera;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.util.ListenerList;

public class OpenCVCamera implements Runnable {
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
	
	private VideoCapture _capture;
	
	private volatile Thread _workerThread;
	
	private Mat _frame;
	
	private int _positionX;
	private int _positionY;
	private int _zoom;
	
	private Size _patternSize;
	
	private volatile boolean _positioning;
	private MatOfPoint2f _cheesboardCorners;
	private Mat _homography;
	private Mat _positioningFrame;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public OpenCVCamera(int positionX, int positionY, int zoom) {
		_listenerList = new OpenCVCameraListenerList();
		
		_positionX = positionX;
		_positionY = positionY;
		_zoom = zoom;
		
		_capture = new VideoCapture();
		_capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
		_capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
		
		_patternSize = new Size(7, 7);
	}
	
	public ListenerList<OpenCVCameraListener> getListenerList() {
		return _listenerList;
	}
	
	public Size getPatternSize() {
		return _patternSize;
	}
	
	public Mat getFrame() {
		Mat frame = _frame;
		if (frame != null) {
			return frame.clone();
		} else {
			return null;
		}
	}
	
	private void setFrame(Mat frame) {
		Mat m = _frame;
		
		if (frame != null && frame.empty()) {
			frame = null;
		}
		
		_frame = frame;
		_listenerList.onNewFrame();
		
		if (m != null) {
			m.release();
		}
	}
	
	public void setMap(Map map, int offsetX, int offsetY) {
		// from here you are allowed to detect cars.
	}
	
	public void openCamera(int device) {
		if (_capture.isOpened()) {
			_capture.release();
		}
		
		if (_workerThread != null) {
			_workerThread = null;
		}
		
		if (_capture.open(device)) {
			startWorkerThread();
		}
	}
	
	private void startWorkerThread() {
		_workerThread = new Thread(this, "Camera capture");
		_workerThread.setDaemon(true);
		_workerThread.start();
	}
	
	public boolean beginPositioning() {
		Mat m = getFrame();
		MatOfPoint2f corners = new MatOfPoint2f();
		if (m != null && Calib3d.findChessboardCorners(m, getPatternSize(), corners)) {
			_positioning = true;
			_workerThread = null;
			
			_positioningFrame = m;
			
			Mat tl = corners.row(0);
			Mat tr = corners.row((int) (getPatternSize().area() - getPatternSize().width));
			Mat br = corners.row((int) (getPatternSize().area() - 1));
			Mat bl = corners.row((int) (getPatternSize().width - 1));
			
			_cheesboardCorners = new MatOfPoint2f();
			_cheesboardCorners.push_back(tl);
			_cheesboardCorners.push_back(tr);
			_cheesboardCorners.push_back(br);
			_cheesboardCorners.push_back(bl);
			
			updateHomography();
			
			return true;
		}
		return false;
	}
	
	private void updateHomography() {
		if (!_positioning) {
			return;
		}
		
		MatOfPoint2f xImage = new MatOfPoint2f();
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + 0, _positionY + 0)));
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + _zoom, _positionY + 0)));
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + _zoom, _positionY + _zoom)));
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + 0, _positionY + _zoom)));
		
		_homography = Calib3d.findHomography(_cheesboardCorners, xImage);
		
		Mat dst = new Mat();
		Imgproc.warpPerspective(_positioningFrame, dst, _homography, _positioningFrame.size());
		setFrame(dst);
	}
	
	public void setPosition(int x, int y) {
		_positionX = x;
		_positionY = y;
		
		updateHomography();
	}
	
	public void setZoom(int zoom) {
		_zoom = zoom;
		
		updateHomography();
	}
	
	public void startCalibration() {
		_positioning = false;
		_positioningFrame.release();
		_positioningFrame = null;
		_cheesboardCorners = null;
		
		startWorkerThread();
	}
	
	public void endCalibration() {
		
	}
	
	@Override
	public void run() {
		try {
			while (_workerThread == Thread.currentThread() && _capture.isOpened()) {
				Mat mat = new Mat();
				_capture.read(mat);
				if (!_positioning) {
					Mat homography = _homography;
					if (homography != null) {
						Mat dst = new Mat();
						Imgproc.warpPerspective(mat, dst, homography, mat.size());
						mat.release();
						mat = dst;
					}
					
					setFrame(mat);
				}
			}
		} finally {
			if (_workerThread == Thread.currentThread()) {
				setFrame(null);
			}
		}
	}
	
	public void stop() {
		_workerThread = null;
		_capture.release();
	}
	
	public void rotate() {
		if (_cheesboardCorners == null || _cheesboardCorners.empty()) {
			return;
		}
		
		MatOfPoint2f m = new MatOfPoint2f();
		m.push_back(_cheesboardCorners.row(1));
		m.push_back(_cheesboardCorners.row(2));
		m.push_back(_cheesboardCorners.row(3));
		m.push_back(_cheesboardCorners.row(0));
		_cheesboardCorners = m;
		
		updateHomography();
	}
}
