package at.fhv.audioracer.camera;

import java.util.ArrayList;
import java.util.List;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import at.fhv.audioracer.core.model.Car;
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
	
	private boolean _detectCars = false;
	private boolean _drawTriangels = true;
	
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
	private Point _offsetPoint;
	
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
		
		if (_detectCars && frame != null) {
			findCars(frame);
		}
		
		_frame = frame;
		_listenerList.onNewFrame();
		
		if (m != null) {
			m.release();
		}
	}
	
	public void setMap(Map map, int offsetX, int offsetY) {
		// from here you are allowed to detect cars.
		// TODO do something with parameters
		
		_detectCars = true;
		_offsetPoint = new Point(offsetX, offsetY);
	}
	
	private void findCars(Mat frame) {
		// there is a faster way with caching processed images
		List<MatOfPoint> triangles = findPolygons(frame, 3);
		List<MatOfPoint> rectangles = findPolygons(frame, 4);
		List<MatOfPoint> candidates = new ArrayList<MatOfPoint>();
		for (MatOfPoint triangle : triangles) {
			double triangleArea = Imgproc.contourArea(triangle);
			if (_drawTriangels) {
				Point[] points = triangle.toArray();
				Scalar color = new Scalar(0, 0, 255);
				
				for (int i = 0; i < 2; i++) {
					Core.line(frame, points[i], points[i + 1], color);
				}
				Core.line(frame, points[2], points[0], color);
			}
			for (MatOfPoint rectangle : rectangles) {
				if (Imgproc.contourArea(rectangle) > triangleArea) {
					// check if triangle is within rectangel
					Point[] points = triangle.toArray();
					boolean inside = true;
					for (int i = 0; i < 3; i++) {
						if (0 > Imgproc.pointPolygonTest(new MatOfPoint2f(rectangle.toArray()),
								points[i], false)) {
							inside = false;
							break;
						}
					}
					
					// triangle is within a bigger rectangle
					if (inside) {
						candidates.add(triangle);
					}
				}
			}
		}
		// TODO delete test code for drawing candidates
		Core.fillPoly(frame, candidates, new Scalar(0, 0, 255));
		
		java.util.Map<Car, MatOfPoint> cars = mapCandidatesToCars(candidates);
		
		calcCarDirection(cars);
		
	}
	
	private void calcCarDirection(java.util.Map<Car, MatOfPoint> cars) {
		// TODO Auto-generated method stub
		
	}
	
	private java.util.Map<Car, MatOfPoint> mapCandidatesToCars(List<MatOfPoint> candidates) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * finds polygons with same number of corners defined
	 * 
	 * @param frame
	 *            image to find polygons
	 * @param corners
	 *            number of corners
	 * @return List of found polygons
	 */
	private List<MatOfPoint> findPolygons(Mat frame, int corners) {
		
		Mat imgGrayScale = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		
		Imgproc.cvtColor(frame, imgGrayScale, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(imgGrayScale, imgGrayScale, 184, 255, Imgproc.THRESH_BINARY);
		
		// find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> polygons = new ArrayList<MatOfPoint>();
		
		// find only black objects
		// Mat imgHSV = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		// Imgproc.cvtColor(frame, imgHSV, Imgproc.COLOR_BGR2HSV);
		
		// Mat imgTreshold = new Mat(imgHSV.size(), Core.DEPTH_MASK_8U, new Scalar(1));
		// Core.inRange(imgHSV, new Scalar(0, 50, 200), new Scalar(179, 0, 255), imgTreshold);
		
		// Imgproc.GaussianBlur(imgTreshold, imgTreshold, new Size(5, 5), 5, 5);
		
		Imgproc.findContours(imgGrayScale, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_SIMPLE);
		
		for (MatOfPoint contour : contours) {
			
			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			Imgproc.approxPolyDP(contour2f, contour2f, Imgproc.arcLength(contour2f, true) * 0.1,
					true);
			
			// find triangles and mark them with blue lines
			if (contour2f.total() == corners) {
				polygons.add(contour);
			}
		}
		return polygons;
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
