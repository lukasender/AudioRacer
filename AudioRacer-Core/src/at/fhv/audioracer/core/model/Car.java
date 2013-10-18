package at.fhv.audioracer.core.model;

import java.awt.image.BufferedImage;

import at.fhv.audioracer.core.util.ListenerList;

public class Car {
	private static class CarListenerList extends ListenerList<ICarListener> implements ICarListener {
		@Override
		public void onCarPositionChanged(Car car) {
			for (ICarListener listener : listeners()) {
				listener.onCarPositionChanged(car);
			}
		}
	}
	
	private final int _cameraId;
	
	private float _posX;
	private float _posY;
	private float _direction;
	
	private final BufferedImage _image;
	
	private CarListenerList _listenerList;
	
	public Car(int cameraId, float posX, float posY, float direction, BufferedImage image) {
		_cameraId = cameraId;
		_posX = posX;
		_posY = posY;
		_direction = direction;
		
		_image = image;
		
		_listenerList = new CarListenerList();
	}
	
	public int getCameraId() {
		return _cameraId;
	}
	
	public float getPosX() {
		return _posX;
	}
	
	public float getPosY() {
		return _posY;
	}
	
	public float getDirection() {
		return _direction;
	}
	
	public void updatePosition(float posX, float posY, float direction) {
		_posX = posX;
		_posY = posY;
		_direction = direction;
		_listenerList.onCarPositionChanged(this);
	}
	
	public BufferedImage getImage() {
		return _image;
	}
	
	public ListenerList<ICarListener> getCarListenerList() {
		return _listenerList;
	}
}
