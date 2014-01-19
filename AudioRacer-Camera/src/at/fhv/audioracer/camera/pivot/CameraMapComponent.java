package at.fhv.audioracer.camera.pivot;

import java.awt.image.BufferedImage;

import org.apache.pivot.wtk.Mouse;
import org.apache.pivot.wtk.Mouse.Button;
import org.apache.pivot.wtk.Mouse.ScrollType;
import org.opencv.calib3d.Calib3d;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint2f;

import at.fhv.audioracer.camera.OpenCVCamera;
import at.fhv.audioracer.camera.OpenCVCameraListener;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.ui.pivot.MapComponent;

public class CameraMapComponent extends MapComponent implements OpenCVCameraListener {
	private OpenCVCamera _camera;
	
	private boolean _drawCheesboard;
	
	private boolean _positioning;
	private int _zoom;
	private int _positionX;
	private int _positionY;
	
	private Position _lastMousePosition; // TODO: use a position which uses integers as values
	private Position _firstCorner;
	private Position _secondCorner;
	
	private boolean _selecting;
	
	public CameraMapComponent() {
		setSkin(new CameraMapComponentSkin(this));
		
		_positionX = 0;
		_positionY = 0;
		_zoom = 50;
		_camera = new OpenCVCamera(_positionX, _positionY, _zoom);
		_camera.getListenerList().add(this);
		CameraApplication.getInstance().setCamera(_camera);
		
		_drawCheesboard = false;
	}
	
	public BufferedImage getCameraImage() {
		Mat frame = _camera.getFrame();
		if (frame == null) {
			return null;
		} else {
			if (_drawCheesboard) {
				MatOfPoint2f corners = new MatOfPoint2f();
				if (Calib3d.findChessboardCorners(frame, _camera.getPatternSize(), corners)) {
					Calib3d.drawChessboardCorners(frame, _camera.getPatternSize(), corners, true);
				}
			}
			
			BufferedImage img = matToBufferedImage(frame);
			frame.release();
			return img;
		}
	}
	
	public boolean isDrawCheesboard() {
		return _drawCheesboard;
	}
	
	public void setDrawCheesboard(boolean drawCheesboard) {
		_drawCheesboard = drawCheesboard;
	}
	
	public Position getFirstCorner() {
		return _firstCorner;
	}
	
	public Position getSecondCorner() {
		return _secondCorner;
	}
	
	public void selectCamera(int id) {
		_camera.openCamera(id);
	}
	
	public boolean startPositioning() {
		if (_camera.beginPositioning()) {
			_positioning = true;
			return true;
		} else {
			return false;
		}
	}
	
	public void startCalibration() {
		_positioning = false;
		_camera.startCalibration();
	}
	
	public void endCalibration() {
		_camera.endCalibration();
		_selecting = true;
	}
	
	@Override
	public void onNewFrame() {
		repaint();
	}
	
	@Override
	protected boolean mouseDown(Button button, int xArgument, int yArgument) {
		if (_positioning && button == Button.LEFT) {
			_lastMousePosition = new Position(xArgument, yArgument);
			return true;
		} else if (_selecting && button == Button.LEFT) {
			_firstCorner = new Position(xArgument, yArgument);
			_secondCorner = _firstCorner;
			return true;
		} else {
			return super.mouseDown(button, xArgument, yArgument);
		}
	}
	
	@Override
	protected boolean mouseMove(int xArgument, int yArgument) {
		if (_positioning && _lastMousePosition != null) {
			if (!Mouse.isPressed(Button.LEFT)) {
				_lastMousePosition = null;
			} else {
				_positionX += (xArgument - _lastMousePosition.getPosX());
				_positionY += (yArgument - _lastMousePosition.getPosY());
				_camera.setPosition(_positionX, _positionY);
				
				_lastMousePosition.setPosX(xArgument);
				_lastMousePosition.setPosY(yArgument);
			}
			
			return true;
		} else if (_selecting && Mouse.isPressed(Button.LEFT)) {
			_secondCorner = new Position(xArgument, yArgument);
			return true;
		} else {
			return super.mouseMove(xArgument, yArgument);
		}
	}
	
	@Override
	protected boolean mouseUp(Button button, int xArgument, int yArgument) {
		if (_positioning && button == Button.LEFT) {
			_lastMousePosition = null;
			return true;
		} else {
			return super.mouseUp(button, xArgument, yArgument);
		}
	}
	
	@Override
	protected boolean mouseWheel(ScrollType scrollType, int scrollAmount, int wheelRotation,
			int xArgument, int yArgument) {
		
		if (_positioning) {
			_zoom -= wheelRotation;
			_camera.setZoom(_zoom);
			return true;
		} else {
			return super.mouseWheel(scrollType, scrollAmount, wheelRotation, xArgument, yArgument);
		}
	}
	
	public void rotate() {
		_camera.rotate();
	}
	
	/**
	 * Converts/writes a Mat into a BufferedImage.
	 * 
	 * @param matrix
	 *            Mat of type CV_8UC3 or CV_8UC1
	 * @return BufferedImage of type TYPE_3BYTE_BGR or TYPE_BYTE_GRAY
	 */
	private BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
			case 1:
				type = BufferedImage.TYPE_BYTE_GRAY;
				break;
			case 3:
				type = BufferedImage.TYPE_3BYTE_BGR;
				// bgr to rgb
				byte b;
				for (int i = 0; i < data.length; i = i + 3) {
					b = data[i];
					data[i] = data[i + 2];
					data[i + 2] = b;
				}
				break;
			default:
				return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}
}
