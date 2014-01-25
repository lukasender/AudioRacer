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
	
	private List<Mat> _calibrationPoints;
	private Mat _cameraMatrix;
	private Mat _distCoeefs;
	
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
}
