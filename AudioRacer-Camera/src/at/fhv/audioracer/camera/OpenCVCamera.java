package at.fhv.audioracer.camera;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

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
	private boolean _drawTriangels = false;
	
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
	
	private Panel _p;
	private long _lastFrame;
	private Scalar _lowerBound;
	private Scalar _upperBound;
	private int _frameCounter;
	List<OpenCVCameraCar> _cameraCars;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public OpenCVCamera(int positionX, int positionY, int zoom) {
		_listenerList = new OpenCVCameraListenerList();
		
		_positionX = positionX;
		_positionY = positionY;
		_zoom = zoom;
		
		_capture = new VideoCapture();
		_capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 1024);
		_capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 768);
		
		_patternSize = new Size(4, 6);
		
		JFrame frame = new JFrame("BasicPanel");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		_p = new Panel();
		frame.setContentPane(_p);
		frame.setVisible(true);
		
		_frameCounter = 0;
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
		
		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - _lastFrame;
		_lastFrame = currentTime;
		
		// calc frames per second
		if (_frameCounter == 20) {
			_frameCounter = 1;
			System.out.println("FPS: " + (1000 / deltaTime));
		} else {
			_frameCounter++;
		}
		
		if (frame != null && frame.empty()) {
			frame = null;
		}
		
		if (_detectCars && frame != null) {
			detectCar(frame);
		}
		
		_frame = frame;
		_listenerList.onNewFrame();
		
		if (m != null) {
			m.release();
		}
	}
	
	/**
	 * 
	 * @param map
	 * @param offsetX
	 *            origin of map on image
	 * @param offsetY
	 *            origin of map on image
	 */
	public void setMap(Map map, int offsetX, int offsetY) {
		// from here you are allowed to detect cars.
		// TODO do something with parameters
		
		_detectCars = true;
		_offsetPoint = new Point(offsetX, offsetY);
	}
	
	// finds every car defined in _cameraCars and updates position and direction
	private void findCars(Mat frame) {
		// TODO implement
	}
	
	/**
	 * trys to detect one car specified with a huerange of trianglemarker
	 * 
	 * @return
	 */
	private boolean detectCar(Mat frame) {
		if (_cameraCars == null) {
			List<OpenCVCameraCar> _cameraCars = new ArrayList<OpenCVCameraCar>();
		}
		
		if (_lowerBound == null || _upperBound == null) {
			// TODO do something
			return false;
		}
		
		// convert to hue
		Mat imgHue = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		// Imgproc.medianBlur(frame, frame, 5);
		Imgproc.cvtColor(frame, imgHue, Imgproc.COLOR_BGR2HSV);
		
		List<MatOfPoint> triangles = findCarByColor(imgHue, _lowerBound, _upperBound);
		
		if (triangles.size() == 1) {
			Point[] points = triangles.get(0).toArray();
			Scalar color = new Scalar(0, 0, 255);
			
			for (int i = 0; i < 2; i++) {
				Core.line(frame, points[i], points[i + 1], color);
			}
			Core.line(frame, points[2], points[0], color);
			Core.fillPoly(frame, triangles, new Scalar(0, 0, 255));
			return true;
		}
		return false;
	}
	
	private List<MatOfPoint> findCarByColor(Mat hueFrame, Scalar lowerBound, Scalar upperBound) {
		
		Mat imgTreshHold = new Mat(hueFrame.size(), Core.DEPTH_MASK_8U, new Scalar(1));
		Core.inRange(hueFrame, lowerBound, upperBound, imgTreshHold);
		Imgproc.erode(imgTreshHold, imgTreshHold,
				Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3, 3)));
		Imgproc.dilate(imgTreshHold, imgTreshHold,
				Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3)));
		
		_p.setImage(imgTreshHold);
		
		// return all triangles with defined color
		return findPolygons(imgTreshHold, 3);
		
	}
	
	@Deprecated
	private void findCarsWithPolygons(Mat frame) {
		// convert to grayscale
		Mat imgGrayScale = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		Mat imgBilat = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		Imgproc.cvtColor(frame, imgGrayScale, Imgproc.COLOR_BGR2GRAY);
		
		Imgproc.bilateralFilter(imgGrayScale, imgBilat, 5, 1, 100);
		
		Imgproc.threshold(imgBilat, imgGrayScale, 128, 255, Imgproc.THRESH_BINARY);
		
		_p.setImage(imgGrayScale);
		
		List<MatOfPoint> triangles = findPolygons(imgGrayScale, 3);
		List<MatOfPoint> rectangles = findPolygons(imgGrayScale, 4);
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
					
					if (_drawTriangels) {
						points = rectangle.toArray();
						Scalar color = new Scalar(0, 255, 0);
						
						for (int i = 0; i < 3; i++) {
							Core.line(frame, points[i], points[i + 1], color);
						}
						Core.line(frame, points[3], points[0], color);
					}
					
					// triangle is within a bigger rectangle
					if (inside) {
						candidates.add(triangle);
					}
				}
			}
		}
		// TODO delete test code for drawing candidates
		// if (_drawTriangels) {
		// Core.fillPoly(frame, rectangles, new Scalar(0, 255, 0));
		// Core.fillPoly(frame, triangles, new Scalar(0, 0, 255));
		Core.fillPoly(frame, candidates, new Scalar(0, 0, 255));
		// }
		
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
		
		// find contours
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
		List<MatOfPoint> polygons = new ArrayList<MatOfPoint>();
		
		Imgproc.findContours(frame, contours, new Mat(), Imgproc.RETR_LIST,
				Imgproc.CHAIN_APPROX_TC89_KCOS);
		
		for (MatOfPoint contour : contours) {
			
			MatOfPoint2f contour2f = new MatOfPoint2f(contour.toArray());
			Imgproc.approxPolyDP(contour2f, contour2f, Imgproc.arcLength(contour2f, true) * 0.1,
					true);
			
			// find specified polygons and add them to list of polygons
			if (contour2f.total() == corners) {
				polygons.add(new MatOfPoint(contour2f.toArray()));
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
		
		Mat imgBilat = getFrame();
		Imgproc.bilateralFilter(imgBilat, m, 5, 1, 100);
		
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
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + _zoom, _positionY + _zoom
				* (4.0 / 6.0))));
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + 0, _positionY + _zoom
				* (4.0 / 6.0))));
		
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
	
	public void updateHueRange(int colorLower, int colorUpper, int saturationLower,
			int saturationUpper, int valueLower, int valueUpper) {
		
		_lowerBound = new Scalar(colorLower, saturationLower, valueLower);
		_upperBound = new Scalar(colorUpper, saturationUpper, valueUpper);
		
	}
}
