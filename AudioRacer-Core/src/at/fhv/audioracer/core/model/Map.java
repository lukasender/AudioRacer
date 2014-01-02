package at.fhv.audioracer.core.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import at.fhv.audioracer.core.util.ListenerList;

public class Map {

	private static class MapListenerList extends ListenerList<IMapListener>
			implements IMapListener {
		@Override
		public void onMapSizeChanged() {
			for (IMapListener listener : listeners()) {
				listener.onMapSizeChanged();
			}
		}

		@Override
		public void onCarAdded(Car addedCar) {
			for (IMapListener listener : listeners()) {
				listener.onCarAdded(addedCar);
			}
		}

		@Override
		public void onCarRemoved(Car removedCar) {
			for (IMapListener listener : listeners()) {
				listener.onCarRemoved(removedCar);
				;
			}
		}

		@Override
		public void onCheckpointChange() {
			for (IMapListener listener : listeners()) {
				listener.onCheckpointChange();
			}

		}
	}

	private int _sizeX;
	private int _sizeY;

	private java.util.Map<Byte, Car> _cars;
	private ArrayList<Checkpoint> _checkpoints;

	private MapListenerList _listenerList;

	public Map(int sizeX, int sizeY) {
		_sizeX = sizeX;
		_sizeY = sizeY;

		_cars = new HashMap<Byte, Car>();
		_checkpoints = new ArrayList<Checkpoint>();

		_listenerList = new MapListenerList();
	}

	public int getSizeX() {
		return _sizeX;
	}

	public void setSizeX(int sizeX) {
		if (_sizeX != sizeX) {
			_sizeX = sizeX;
			_listenerList.onMapSizeChanged();
		}
	}

	public int getSizeY() {
		return _sizeY;
	}

	public void setSizeY(int sizeY) {
		if (_sizeY != sizeY) {
			_sizeY = sizeY;
			_listenerList.onMapSizeChanged();
		}
	}

	public void addCar(Car car) {
		if (car == null) {
			throw new IllegalArgumentException("Car must not be null.");
		}

		if (_cars.containsKey(car.getCarId())) {
			removeCar(car.getCarId());
		}

		_cars.put(car.getCarId(), car);
		_listenerList.onCarAdded(car);
	}

	public Car removeCar(int cameraId) {
		Car car = _cars.remove(cameraId);
		if (car != null) {
			_listenerList.onCarRemoved(car);
		}
		return car;
	}

	public void addCheckpoint(Checkpoint addedCP) {
		if (!_checkpoints.contains(addedCP)) {
			_checkpoints.add(addedCP);
			_listenerList.onCheckpointChange();
		}
	}

	public void removeCheckpoint(Checkpoint removedCP) {
		if (_checkpoints.contains(removedCP)) {
			_checkpoints.remove(removedCP);
			_listenerList.onCheckpointChange();
		}
	}

	public Collection<Car> getCars() {
		return Collections.unmodifiableCollection(_cars.values());
	}

	public Collection<Checkpoint> getCheckpoints() {
		return Collections.unmodifiableCollection(_checkpoints);
	}

	public ListenerList<IMapListener> getMapListenerList() {
		return _listenerList;
	}
}
