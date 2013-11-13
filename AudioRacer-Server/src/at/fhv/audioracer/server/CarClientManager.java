package at.fhv.audioracer.server;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarClientManager;

public class CarClientManager implements ICarClientManager {
	
	private static CarClientManager _instance;
	
	private CarClientManager() {
	}
	
	public static synchronized CarClientManager getInstance() {
		if (_instance == null) {
			_instance = new CarClientManager();
		}
		return _instance;
	}
	
	@Override
	public void connect(ICarClient car) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void disconnect(ICarClient car) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void invalidCommand() {
		// TODO Auto-generated method stub
		
	}
	
}
