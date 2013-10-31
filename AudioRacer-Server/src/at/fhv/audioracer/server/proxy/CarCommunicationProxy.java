package at.fhv.audioracer.server.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarClientManager;

import com.esotericsoftware.kryonet.Connection;

public class CarCommunicationProxy extends Connection implements ICarClientManager, ICarClient {
	
	private ICarClient _carClient;
	private boolean _connected = false;
	private static Logger _logger = LoggerFactory.getLogger(CarCommunicationProxy.class);
	private int _carId;
	
	public CarCommunicationProxy(int carId) {
		_carId = carId; // for debugging only
	}
	
	public ICarClient getCarClient() {
		return _carClient;
	}
	
	public void setCarClient(ICarClient carClient) {
		_carClient = carClient;
	}
	
	@Override
	public void connect(ICarClient car) {
		_connected = true;
		_logger.debug("Connect: {}", _carId);
	}
	
	@Override
	public void disconnect(ICarClient car) {
		_connected = false;
	}
	
	@Override
	public void invalidCommand() {
		_logger.warn("invalidCommand!");
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		// if (_connected) {
		_carClient.updateVelocity(speed, direction);
		// }
	}
	
}
