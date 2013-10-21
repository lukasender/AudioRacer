package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.fhv.audioracer.core.model.Car;

public class CarManager {
	private static final Map<Integer, Car> _carList = Collections.synchronizedMap(new HashMap<Integer, Car>());
	private static final Map<Integer, Car> _freeCars = Collections.synchronizedMap(new HashMap<Integer, Car>());
	
	public static void addFreeCar(int carId, Car car) {
		_freeCars.put(carId, car);
	}
	
	public static void getFreeCar(int carId) {
		_freeCars.get(carId);
	}
}
