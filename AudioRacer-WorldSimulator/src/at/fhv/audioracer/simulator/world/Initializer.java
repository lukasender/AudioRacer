package at.fhv.audioracer.simulator.world;

import javax.naming.OperationNotSupportedException;

import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.ui.pivot.MapComponent;

// TODO: This class name may change.
/**
 * Takes care of the initialization of the logic of the simulator.
 * 
 * @author lumannnn
 * 
 */
public class Initializer {
	
	private MapComponent _map;
	
	private static Initializer _instance;
	
	private Initializer() {
	}
	
	public static Initializer getInstance() {
		if (_instance == null) {
			_instance = new Initializer();
		}
		
		return _instance;
	}
	
	public void setUp(MapComponent mapComponent, Map map) throws OperationNotSupportedException {
		setMap(mapComponent);
		getMap().setMap(map);
	}
	
	public void setMap(MapComponent map) {
		if (map == null) {
			throw new IllegalArgumentException("map must not be null");
		}
		_map = map;
	}
	
	public MapComponent getMap() {
		return _map;
	}
	
	public void update() {
		// int x = getMap().getMap().getSizeX();
		// int y = getMap().getMap().getSizeY();
		// System.out.println("MapSize: " + x + ", " + y);
		
	}
	
}
