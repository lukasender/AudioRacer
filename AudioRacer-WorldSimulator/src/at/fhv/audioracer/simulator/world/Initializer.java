package at.fhv.audioracer.simulator.world;

import java.io.IOException;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.ui.pivot.MapComponent;

// TODO: This class name may change.
/**
 * Takes care of the initialization of the logic of the simulator.
 * 
 * @author lumannnn
 * 
 */
public class Initializer {
	
	private static Initializer _instance;
	
	private MapComponent _map;
	
	private int _carId;
	
	private Position _lastCarPos;
	private static float TRANSLATE_BY = 2;
	
	private Initializer() {
		_carId = 0;
		_lastCarPos = new Position(0, 0);
	}
	
	public static Initializer getInstance() {
		if (_instance == null) {
			_instance = new Initializer();
		}
		
		return _instance;
	}
	
	public void setUp(MapComponent mapComponent, Map map) throws OperationNotSupportedException {
		setMap(mapComponent);
		getMapComponent().setMap(map);
	}
	
	public void setMap(MapComponent map) {
		if (map == null) {
			throw new IllegalArgumentException("map must not be null");
		}
		_map = map;
	}
	
	public MapComponent getMapComponent() {
		return _map;
	}
	
	public void addCar() {
		try {
			Car car = new Car(_carId++, _lastCarPos, new Direction(90), ImageIO.read(MapComponent.class.getResource("car-red.png")));
			translateLasCarPosX(TRANSLATE_BY);
			getMap().addCar(car);
			System.out.println("added car with id: " + (_carId - 1));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void removeCar() {
		if (_carId > 0) {
			translateLasCarPosX(-TRANSLATE_BY);
			getMap().removeCar(--_carId);
			System.out.println("removed car with id: " + (_carId));
		}
	}
	
	private Map getMap() {
		return getMapComponent().getMap();
	}
	
	public void update() {
		// int x = getMap().getMap().getSizeX();
		// int y = getMap().getMap().getSizeY();
		// System.out.println("MapSize: " + x + ", " + y);
		
	}
	
	// helper methods
	
	public void translateLasCarPosX(float x) {
		_lastCarPos = new Position(_lastCarPos.getPosX() + x, 0);
	}
	
}
