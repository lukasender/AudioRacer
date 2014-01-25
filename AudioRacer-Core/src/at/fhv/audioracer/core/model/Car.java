package at.fhv.audioracer.core.model;

import java.awt.image.BufferedImage;

import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.ListenerList;
import at.fhv.audioracer.core.util.Position;

public class Car<T> {

	public static byte CAR_CLIENT_NOT_ASSIGNED_ID = -1;

	private static class CarListenerList extends ListenerList<ICarListener>
			implements ICarListener {
		@Override
		public void onCarPositionChanged(Car<?> car) {
			for (ICarListener listener : listeners()) {
				listener.onCarPositionChanged(car);
			}
		}
	}

	private final byte _carId;

	private byte _carClientId = CAR_CLIENT_NOT_ASSIGNED_ID;

	private Position _position;
	/**
	 * Absolute direction in degrees (0-360°).
	 */
	private Direction _direction;

	private T _player;

	private final BufferedImage _image;

	private CarListenerList _listenerList;

	/**
	 * @deprecated - there is a reason for why <code>BufferedImage</code> is
	 *             final! Don't use this constructor anymore. Remove this
	 *             constructor if you've changed your code (who ever is using
	 *             it. I don't know).
	 * @param cardId
	 */
	@Deprecated
	public Car(byte cardId) {
		_carId = cardId;
		_image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB); // this is
																		// not
																		// useful
																		// at
																		// all.
	}

	public Car(byte carId, Position position, Direction direction,
			BufferedImage image) {

		// TODO remove comment for null check of BufferdImage
		// if (image == null) {
		// throw new IllegalArgumentException(
		// "An image ('BufferedImage') is required");
		// }
		_carId = carId;
		_position = position;
		_direction = direction;

		_image = image;

		_listenerList = new CarListenerList();
	}

	public byte getCarId() {
		return _carId;
	}

	public Position getPosition() {
		return _position;
	}

	public Direction getDirection() {
		return _direction;
	}

	public T getPlayer() {
		return _player;
	}

	public void setPlayer(T player) {
		_player = player;
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

	public byte getCarClientId() {
		return _carClientId;
	}

	public void setCarClientId(byte carClientId) {
		_carClientId = carClientId;
	}

	@Override
	public String toString() {
		return String.valueOf(_carId);
	}
}
