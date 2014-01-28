package at.fhv.audioracer.camera;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JFrame;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.ListenerList;
import at.fhv.audioracer.core.util.Position;

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
	
	private boolean _detectCar = false;
	private boolean _directionConfigured = false;
	private boolean _drawTriangels = false;
	
	private OpenCVCameraListenerList _listenerList;
	
	private VideoCapture _capture;
	
	private volatile Thread _workerThread;
	
	private Mat _frame;
	
	private int _positionX;
	private int _positionY;
	private int _zoom;
	private int _rotation;
	
	private Size _patternSize;
	
	private volatile boolean _positioning;
	private MatOfPoint2f _cheesboardCorners;
	private Mat _homography;
	private Mat _positioningFrame;
	private Point _offsetPoint;
	
	private List<Mat> _calibrationPoints;
	private Mat _cameraMatrix;
	private Mat _distCoeefs;
	
	private Panel _p;
	private Mat _pImage; // threshHold image
	private Panel _testPanel;
	private Mat _testImage; // testImage
	private long _lastFrame;
	private int _frameCounter;
	
	private Scalar _lowerBound;
	private Scalar _upperBound;
	
	private Scalar _lowerDirectionHueBound;
	private Scalar _upperDirectionHueBound;
	
	private byte _id = 0;
	
	private List<OpenCVCameraCar> _cameraCars;
	private Map _map;
	private boolean _allCarsDetected;
	
	static {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
	}
	
	public OpenCVCamera(int positionX, int positionY, int zoom) {
		_listenerList = new OpenCVCameraListenerList();
		
		_positionX = positionX;
		_positionY = positionY;
		_zoom = zoom;
		_rotation = 0;
		
		_capture = new VideoCapture();
		_capture.set(Highgui.CV_CAP_PROP_FRAME_WIDTH, 640);
		_capture.set(Highgui.CV_CAP_PROP_FRAME_HEIGHT, 480);
		
		_patternSize = new Size(4, 6);
		
		JFrame frame = new JFrame("ThreshHoldPanel");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setSize(400, 400);
		_p = new Panel();
		frame.setContentPane(_p);
		frame.setVisible(true);
		
		JFrame testPanelFrame = new JFrame("TestPanel");
		testPanelFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		testPanelFrame.setSize(400, 400);
		_testPanel = new Panel();
		testPanelFrame.setContentPane(_testPanel);
		testPanelFrame.setVisible(true);
		
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
		
		if (_detectCar && frame != null) {
			detectCar(frame, false);
			_p.repaint();
		}
		
		if (_allCarsDetected) {
			updateCars(frame);
		}
		
		_frame = frame;
		_listenerList.onNewFrame();
		
		if (m != null) {
			m.release();
		}
	}
	
	private void updateCars(Mat frame) {
		if (frame == null) {
			return;
		}
		
		for (OpenCVCameraCar car : _cameraCars) {
			_lowerBound = car.getLowerHueBound();
			_upperBound = car.getUpperHueBound();
			Point[] carVectors = calcCarPosition(car, frame);
			if (carVectors == null) {
				return;
			}
			Direction direction = calcDirection(carVectors[1]);
			// convert Point to Position and add offsetOfGameArea
			Position carPosition = new Position((float) (carVectors[0].x - _offsetPoint.x),
					(float) (carVectors[0].y - _offsetPoint.y));
			car.updatePosition(carPosition, direction);
			Core.circle(frame, carVectors[0], 3, new Scalar(255, 0, 255), -1);
			Point carDirectionPoint = new Point(carVectors[0].x + (3 * carVectors[1].x),
					carVectors[0].y + (3 * carVectors[1].y));
			Core.line(frame, carVectors[0], carDirectionPoint, new Scalar(255, 255, 255));
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
		_map = map;
		
		_detectCar = true;
		_offsetPoint = new Point(offsetX, offsetY);
	}
	
	// finds every car defined in _cameraCars and updates position and direction
	private void findCars(Mat frame) {
		// TODO implement
	}
	
	/**
	 * trys to detect one car specified with a huerange
	 * 
	 * @return
	 */
	private synchronized boolean detectCar(Mat frame, boolean draw) {
		
		if (_lowerBound == null || _upperBound == null) {
			// TODO do something
			return false;
		}
		
		// convert to hue
		Mat imgHue = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		Imgproc.cvtColor(frame, imgHue, Imgproc.COLOR_BGR2HSV);
		
		// directionColor configuration mode
		if (!_directionConfigured) {
			
			List<MatOfPoint> directionPolygons = findCarByColor(imgHue, _lowerBound, _upperBound,
					true);
			imgHue.release();
			
			if (directionPolygons != null && directionPolygons.size() > 0) {// draw direction boxes
				Core.fillPoly(frame, directionPolygons, _upperBound);
				
				// draw direction box centers
				for (MatOfPoint direction : directionPolygons) {
					
					Moments moments = Imgproc.moments(direction);
					
					Point center = new Point();
					center.x = moments.get_m10() / moments.get_m00();
					center.y = moments.get_m01() / moments.get_m00();
					
					Core.circle(frame, center, 3, new Scalar(0, 0, 255), -1);
					
				}
			}
			
		} else {
			
			List<MatOfPoint> cars = findCarByColor(imgHue, _lowerBound, _upperBound, true);
			List<MatOfPoint> directionPolygons = findCarByColor(imgHue, _lowerDirectionHueBound,
					_upperDirectionHueBound, false);
			imgHue.release();
			
			if (cars.size() == 1) {
				
				Moments moments = Imgproc.moments(cars.get(0));
				Point carCenter = new Point();
				carCenter.x = moments.get_m10() / moments.get_m00();
				carCenter.y = moments.get_m01() / moments.get_m00();
				
				MatOfPoint carDirection = null;
				Point directionCenter = null;
				double minDist = Double.MAX_VALUE;
				// find nearest directionPolygon
				for (MatOfPoint direction : directionPolygons) {
					
					moments = Imgproc.moments(direction);
					
					Point center = new Point();
					center.x = moments.get_m10() / moments.get_m00();
					center.y = moments.get_m01() / moments.get_m00();
					
					double x = carCenter.x - center.x;
					double y = carCenter.y - center.y;
					
					double distance = x * x + y * y;
					
					if (distance < minDist) {
						directionCenter = center;
						carDirection = direction;
						minDist = distance;
					}
					
				}
				
				List<MatOfPoint> direction = new ArrayList<MatOfPoint>();
				if (carDirection != null) {
					direction.add(carDirection);
				}
				
				// draw car and direction associated to car
				if (cars.size() > 0 && carDirection != null) {
					if (draw) {
						Core.fillPoly(frame, cars, _lowerBound);
						// Core.fillPoly(frame, direction, _lowerDirectionHueBound);
						
						Core.circle(frame, carCenter, 3, new Scalar(0, 0, 255), -1);
						// Core.circle(frame, directionCenter, 3, new Scalar(255, 255, 255), -1);
					}
					
					return true;
				}
				return false;
			}
		}
		
		return false;
	}
	
	private List<MatOfPoint> findCarByColor(Mat hueFrame, Scalar lowerBound, Scalar upperBound,
			boolean draw) {
		
		Mat imgTreshHold = new Mat(hueFrame.size(), Core.DEPTH_MASK_8U, new Scalar(1));
		Core.inRange(hueFrame, lowerBound, upperBound, imgTreshHold);
		Imgproc.erode(imgTreshHold, imgTreshHold,
				Imgproc.getStructuringElement(Imgproc.MORPH_ERODE, new Size(3, 3)));
		Imgproc.dilate(imgTreshHold, imgTreshHold,
				Imgproc.getStructuringElement(Imgproc.MORPH_DILATE, new Size(3, 3)));
		
		if (draw) {
			_p.setImage(imgTreshHold);
		}
		
		List<MatOfPoint> polygons = findPolygons(imgTreshHold, 3);
		imgTreshHold.release();
		
		// return all triangles with defined color
		return polygons;
		
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
		
	}
	
	/**
	 * converts a direction value to Direction Type
	 * 
	 * @param direction
	 * @return Direction as cosine of normalized direction
	 */
	private Direction calcDirection(Point direction) {
		float _tempDirection = 0;
		
		// norm direction
		double norm = Math.sqrt(direction.x * direction.x + direction.y + direction.y);
		double normX = direction.x / norm;
		double normY = direction.y / norm;
		
		_tempDirection = (float) normX;
		
		return new Direction(_tempDirection);
		
	}
	
	/**
	 * 
	 * called from nextCar after CarDetetection was successful
	 * 
	 * @param car
	 * @return Point[0] = position, Point[1] = directionVector
	 */
	private Point[] calcCarPosition(OpenCVCameraCar car, Mat frame) {
		
		// Mat frame = getFrame();
		
		// convert to hue
		Mat imgHue = new Mat(frame.size(), Core.DEPTH_MASK_8U, new Scalar(3));
		Imgproc.cvtColor(frame, imgHue, Imgproc.COLOR_BGR2HSV);
		
		// getCar contour should be one
		List<MatOfPoint> cars = findCarByColor(imgHue, car.getLowerHueBound(),
				car.getUpperHueBound(), false);
		// getDirection contours
		List<MatOfPoint> directionContour = findCarByColor(imgHue, _lowerDirectionHueBound,
				_upperDirectionHueBound, false);
		imgHue.release();
		
		// calculate center points of contours
		if (cars.size() == 1) {
			
			Moments moments = Imgproc.moments(cars.get(0));
			Point carMarkerCenter = new Point();
			carMarkerCenter.x = moments.get_m10() / moments.get_m00();
			carMarkerCenter.y = moments.get_m01() / moments.get_m00();
			
			MatOfPoint carDirectionMarker = null;
			Point directionMarkerCenter = null;
			double minDist = Double.MAX_VALUE;
			// find nearest directionPolygon
			for (MatOfPoint direction : directionContour) {
				
				moments = Imgproc.moments(direction);
				
				Point center = new Point();
				center.x = moments.get_m10() / moments.get_m00();
				center.y = moments.get_m01() / moments.get_m00();
				
				double x = carMarkerCenter.x - center.x;
				double y = carMarkerCenter.y - center.y;
				
				double distance = x * x + y * y;
				
				if (distance < minDist) {
					directionMarkerCenter = center;
					carDirectionMarker = direction;
					minDist = distance;
				}
				
			}
			
			if (directionMarkerCenter == null) {
				return null;
			}
			
			// vector from directionMarker to carMarker
			Point directionToCar = new Point(carMarkerCenter.x - directionMarkerCenter.x,
					carMarkerCenter.y - directionMarkerCenter.y);
			
			// carCenter is theoretically halfway between direction and car markers
			Point carCenter = new Point(directionMarkerCenter.x + directionToCar.x * 0.5,
					directionMarkerCenter.y + directionToCar.y * 0.5);
			
			// rotation counterClockwise by 90 degrees
			Point carDirection = new Point();
			carDirection.x = -directionToCar.y;
			carDirection.y = directionToCar.x;
			
			Point[] returnValues = { carCenter, carDirection };
			
			return returnValues;
			
		}
		
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
		return contours;
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
	
	public void startCalibration() {
		_calibrationPoints = new LinkedList<>();
	}
	
	public boolean calibrationStep() {
		Mat frame = getFrame();
		MatOfPoint2f pointBuf = new MatOfPoint2f();
		boolean found = Calib3d.findChessboardCorners(frame, _patternSize, pointBuf,
				Calib3d.CALIB_CB_ADAPTIVE_THRESH | Calib3d.CALIB_CB_NORMALIZE_IMAGE);
		
		if (found) {
			Mat viewGray = new Mat();
			Imgproc.cvtColor(frame, viewGray, Imgproc.COLOR_BGR2GRAY);
			Imgproc.cornerSubPix(viewGray, pointBuf, new Size(11, 11), new Size(-1, -1),
					new TermCriteria(TermCriteria.EPS | TermCriteria.MAX_ITER, 30, 0.1));
			
			_calibrationPoints.add(pointBuf);
		}
		frame.release();
		
		return found;
	}
	
	public boolean endCalibration() {
		if (!_calibrationPoints.isEmpty() && runCalibration()) {
			return true;
		}
		return false;
	}
	
	private boolean runCalibration() {
		Mat frame = getFrame();
		
		Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
		List<Mat> objectPoints = calcBoardCornerPositions();
		Mat distCoeffs = Mat.zeros(8, 1, CvType.CV_64F);
		List<Mat> rvecs = new LinkedList<>();
		List<Mat> tvecs = new LinkedList<>();
		
		double rms = Calib3d
				.calibrateCamera(objectPoints, _calibrationPoints, frame.size(), cameraMatrix,
						distCoeffs, rvecs, tvecs, Calib3d.CALIB_FIX_K4 | Calib3d.CALIB_FIX_K5);
		
		boolean ok = Core.checkRange(frame) && Core.checkRange(distCoeffs);
		
		if (ok) {
			_cameraMatrix = cameraMatrix;
			_distCoeefs = distCoeffs;
		}
		
		frame.release();
		
		return ok;
	}
	
	private List<Mat> calcBoardCornerPositions() {
		MatOfPoint3f m = new MatOfPoint3f();
		for (int i = 0; i < _patternSize.height; i++) {
			for (int j = 0; j < _patternSize.width; j++) {
				m.push_back(new MatOfPoint3f(new Point3(j, i, 0)));
			}
		}
		
		List<Mat> corners = new LinkedList<>();
		for (int i = 0; i < _calibrationPoints.size(); i++) {
			corners.add(m);
		}
		return corners;
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
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + _zoom, _positionY + _zoom
				* (4.0 / 6.0))));
		xImage.push_back(new MatOfPoint2f(new Point(_positionX + 0, _positionY + _zoom
				* (4.0 / 6.0))));
		
		if (_rotation != 0) {
			Point center = new Point(_positionX + (_zoom / 2), _positionY + (_zoom / 2));
			Mat rotationMat = Imgproc.getRotationMatrix2D(center, _rotation, 1);
			
			MatOfPoint2f dst = new MatOfPoint2f();
			Core.transform(xImage, dst, rotationMat);
			xImage = dst;
		}
		
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
	
	public void setRotation(int degree) {
		_rotation = degree;
		
		updateHomography();
	}
	
	public void endPositioning() {
		_positioning = false;
		_positioningFrame.release();
		_positioningFrame = null;
		_cheesboardCorners = null;
		
		startWorkerThread();
	}
	
	@Override
	public void run() {
		try {
			while (_workerThread == Thread.currentThread() && _capture.isOpened()) {
				Mat mat = new Mat();
				_capture.read(mat);
				
				Mat cameraMatrix = _cameraMatrix;
				Mat distCoeffs = _distCoeefs;
				if (cameraMatrix != null && distCoeffs != null) {
					Mat dst = new Mat();
					Imgproc.undistort(mat, dst, cameraMatrix, distCoeffs);
					mat.release();
					mat = dst;
				}
				
				if (mat != null && !mat.empty()) {
					Mat imgBilat = mat.clone();
					Imgproc.bilateralFilter(imgBilat, mat, 5, 1, 100);
					imgBilat.release();
				}
				
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
	
	public void storeCalibration() throws IOException {
		if (_cameraMatrix != null && _distCoeefs != null) {
			FileOutputStream stream = new FileOutputStream("cameraMatrix.foo");
			storeMatrix(new ObjectOutputStream(stream), _cameraMatrix);
			stream = new FileOutputStream("distCoeefs.foo");
			storeMatrix(new ObjectOutputStream(stream), _distCoeefs);
		}
	}
	
	private void storeMatrix(ObjectOutputStream stream, Mat m) throws IOException {
		Size s = m.size();
		stream.writeDouble(s.width);
		stream.writeDouble(s.height);
		stream.writeInt(m.type());
		for (int i = 0; i < s.height; i++) {
			for (int j = 0; j < s.width; j++) {
				double[] value = m.get(i, j);
				stream.writeObject(value);
			}
		}
		stream.close();
	}
	
	public boolean loadCalibration() throws ClassNotFoundException, IOException {
		File cameraMatrixFile = new File("cameraMatrix.foo");
		File distCoeefsFile = new File("distCoeefs.foo");
		if (cameraMatrixFile.exists() && distCoeefsFile.exists()) {
			FileInputStream stream = new FileInputStream(cameraMatrixFile);
			Mat cameraMatrix = loadMatrix(new ObjectInputStream(stream));
			stream = new FileInputStream(distCoeefsFile);
			Mat distCoeefs = loadMatrix(new ObjectInputStream(stream));
			
			if (cameraMatrix != null && !cameraMatrix.empty() && distCoeefs != null
					&& !distCoeefs.empty()) {
				_cameraMatrix = cameraMatrix;
				_distCoeefs = distCoeefs;
				return true;
			}
		}
		return false;
	}
	
	private Mat loadMatrix(ObjectInputStream stream) throws ClassNotFoundException, IOException {
		Size s = new Size(stream.readDouble(), stream.readDouble());
		Mat m = new Mat(s, stream.readInt());
		for (int i = 0; i < s.height; i++) {
			for (int j = 0; j < s.width; j++) {
				double[] value = (double[]) stream.readObject();
				m.put(i, j, value);
			}
		}
		stream.close();
		return m;
	}
	
	public void updateHueRange(int colorLower, int colorUpper, int saturationLower,
			int saturationUpper, int valueLower, int valueUpper) {
		
		_lowerBound = new Scalar(colorLower, saturationLower, valueLower);
		_upperBound = new Scalar(colorUpper, saturationUpper, valueUpper);
		
	}
	
	/**
	 * 
	 * @return true if there is exactly one car which color is not overlapping with an allready detected car
	 */
	public boolean nextCar() {
		if (_cameraCars == null) {
			_cameraCars = new ArrayList<OpenCVCameraCar>();
		}
		// test currentUpperBound within range of direction color
		if ((_upperBound.val[0] > _lowerDirectionHueBound.val[0] && _upperBound.val[0] < _upperDirectionHueBound.val[0])) {
			return false;
		}
		// test currentLowerBound within range of direction color
		if ((_lowerBound.val[0] > _lowerDirectionHueBound.val[0] && _lowerBound.val[0] < _upperDirectionHueBound.val[0])) {
			return false;
		}
		// test for other cars with similar colors (do not test black/white)
		for (OpenCVCameraCar car : _cameraCars) {
			// test currentUppeBound within range of currentCar
			if ((_upperBound.val[0] > car.getLowerHueBound().val[0] && _upperBound.val[0] < car
					.getUpperHueBound().val[0])) {
				return false;
			}
			// test curretLowerBound within range of currentCar
			if ((_lowerBound.val[0] > car.getLowerHueBound().val[0] && _lowerBound.val[0] < car
					.getUpperHueBound().val[0])) {
				return false;
			}
		}
		
		Mat frame = _frame.clone();
		if (frame == null || frame.empty()) {
			return false;
		}
		
		if (detectCar(frame, false)) {
			byte id = _id++;
			OpenCVCameraCar car = new OpenCVCameraCar();
			car.setHueRange(_lowerBound, _upperBound);
			_cameraCars.add(car);
			Point[] carVectors = calcCarPosition(car, frame);
			
			Direction direction = calcDirection(carVectors[1]);
			// convert Point to Position and add offsetOfGameArea
			Position carPosition = new Position((float) (carVectors[0].x - _offsetPoint.x),
					(float) (carVectors[0].y - _offsetPoint.y));
			car.carDetected(id, carPosition, direction);
			_map.addCar(car.getCar());
			// TODO remove testPanelCode
			Core.circle(frame, carVectors[0], 3, new Scalar(255, 0, 255), -1);
			Point carDirectionPoint = new Point(carVectors[0].x + (3 * carVectors[1].x),
					carVectors[0].y + (3 * carVectors[1].y));
			Core.line(frame, carVectors[0], carDirectionPoint, new Scalar(255, 255, 255));
			_testPanel.setImage(frame);
			
			// try to find car
			return true;
		}
		return false;
	}
	
	public void allCarsDetected() {
		_detectCar = false;
		_allCarsDetected = true;
	}
	
	/**
	 * current hue setting configured as directionHue
	 */
	public void directionHueConfigured() {
		_lowerDirectionHueBound = _lowerBound.clone();
		_upperDirectionHueBound = _upperBound.clone();
		_directionConfigured = true;
	}
}
