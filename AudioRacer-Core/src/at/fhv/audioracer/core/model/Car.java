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
	
	private final int _carId;
	
	private Position _position;
	private Direction _direction;
	
	private final BufferedImage _image;
	
	private CarListenerList _listenerList;
	
	public Car(int carId, Position position, Direction direction, BufferedImage image) {
		_carId = carId;
		_position = position;
		_direction = direction;
		
		_image = image;
		
		_listenerList = new CarListenerList();
	}
	
	public int getCarId() {
		return _carId;
	}
	
	public Position getPosition() {
		return _position;
	}
	
	public Direction getDirection() {
		return _direction;
	}
	
	public void updatePosition(Position position, Direction direction) {
		_position = position;
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
