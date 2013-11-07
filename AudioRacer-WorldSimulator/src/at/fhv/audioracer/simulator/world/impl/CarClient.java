package at.fhv.audioracer.simulator.world.impl;

import at.fhv.audioracer.communication.world.ICamera;
import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;

import com.esotericsoftware.kryonet.Connection;

public class CarClient extends Connection implements ICarClient {
	
	public static class CarClientListenerList
	// extends ListenerList<IListener>
	{
		
	}
	
	private CarClientListenerList _carClientListeners = null;
	
	private ICamera _camera = null; // FIXME: this is probably wrong. add as listener?
	private Car _car = null;
	
	public void setCameraListener(ICamera camera) {
		_camera = camera;
	}
	
	public void setCar(Car car) {
		_car = car;
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		// TODO update model...
		Position pos = _car.getPosition();
		
		_car.updatePosition(pos, new Direction(direction));
	}
	
}
