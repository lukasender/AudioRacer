package at.fhv.audioracer.simulator.proxy;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarClientManager;

import com.esotericsoftware.kryonet.Connection;

public class CarCommunicationProxy extends Connection implements ICarClientManager, ICarClient {
	
	private ICarClientManager _carClientManager;
	private static final Logger _logger = LoggerFactory.getLogger(CarCommunicationProxy.class);
	
	private UUID _carId;
	
	public CarCommunicationProxy() {
		_carId = UUID.randomUUID();
	}
	
	public ICarClientManager getCarClientManager() {
		return _carClientManager;
	}
	
	public void setCarClientManager(ICarClientManager carClientManager) {
		_carClientManager = carClientManager;
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		_logger.debug("updateVelocity - car: {} speed: {} direction: {}", _carId, speed, direction);
	}
	
	@Override
	public void connect() {
		_carClientManager.connect();
	}
	
	@Override
	public void disconnect() {
		_carClientManager.disconnect();
	}
	
	@Override
	public void invalidCommand() {
		_carClientManager.invalidCommand();
	}
}
