package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import at.fhv.audioracer.server.dao.Car;
import at.fhv.audioracer.server.wrappers.GameUnit;

public class CarManager {
	private final Map<Integer, GameUnit> carList = Collections.synchronizedMap(new HashMap<Integer, GameUnit>());
	private final Map<Integer, Car> freeCars = Collections.synchronizedMap(new HashMap<Integer, Car>());
	
	public void addFreeCar(int carId, Car car) {
		freeCars.put(carId, car);
	}
	public void getFreeCar(int carId) {
		freeCars.get(carId);
	}
	/**
	 * @param carId - The carId
	 * @return GameUnit car is part if or null if car is not assigned to a player.
	 */
	public GameUnit getCar(int carId) {
		return carList.get(carId);
	}
}
