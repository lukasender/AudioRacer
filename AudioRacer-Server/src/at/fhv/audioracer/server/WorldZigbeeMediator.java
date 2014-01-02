package at.fhv.audioracer.server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.ICarListener;
import at.fhv.audioracer.core.util.ListenerList;
import at.fhv.audioracer.server.model.ICarManagerListener;
import at.fhv.audioracer.server.model.IWorldZigbeeConnectionCountChanged;

public class WorldZigbeeMediator implements Runnable, ICarListener, ICarManagerListener {
	
	private static class WorldZigbeeConnectionCountListenerList extends
			ListenerList<IWorldZigbeeConnectionCountChanged> implements
			IWorldZigbeeConnectionCountChanged {
		
		@Override
		public void onWorldZigbeeConnectionCountChanged(int oldValue, int newValue) {
			for (IWorldZigbeeConnectionCountChanged listener : listeners()) {
				listener.onWorldZigbeeConnectionCountChanged(oldValue, newValue);
			}
		}
		
	}
	
	private static Logger _logger = LoggerFactory.getLogger(WorldZigbeeMediator.class);
	private BlockingQueue<ICarClient> _awaitingConnectionQueue = new LinkedBlockingQueue<ICarClient>();
	private ICarClient _currentCarClientToConnect = null;
	private Boolean _assignNextCarClient = true;
	private HashMap<Byte, Integer> _updateCarInvocationCount = new HashMap<Byte, Integer>();
	private final int _upateCarInvocationCountThreshold = 1000;
	private int _connectionCount = 0;
	private float _configurationSpeed = 1.f;
	private float _configurationDirection = 0.0f;
	private WorldZigbeeConnectionCountListenerList _listenerList = new WorldZigbeeConnectionCountListenerList();
	
	@Override
	public void run() {
		_logger.info("Mediator thread start.");
		
		try {
			float speed = _configurationSpeed;
			while (true) {
				speed *= -1.f;
				if (_assignNextCarClient) {
					_assignNextCarClient = false;
					_currentCarClientToConnect = _awaitingConnectionQueue.take();
				} else {
					Thread.sleep(50);
					// _logger.debug("try to assign CarClient ... send update velocity.");
					_currentCarClientToConnect.updateVelocity(speed, _configurationDirection);
					
				}
			}
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		_logger.info("Mediator thread exit.");
	}
	
	public int getConnectionCount() {
		return _connectionCount;
	}
	
	@Override
	public void onCarPositionChanged(Car car) {
		int count = 0;
		byte carId = car.getCarId();
		if (_updateCarInvocationCount.containsKey(carId) == false) {
			count = 1;
			_updateCarInvocationCount.put(carId, count);
		} else {
			count = _updateCarInvocationCount.get(carId);
			count++;
			_updateCarInvocationCount.put(car.getCarId(), count);
		}
		// _logger.debug("try to assign CarClient with id: {} current updatePos count: {}", carId,
		// count);
		
		if (count > _upateCarInvocationCountThreshold
				&& car.getCarClientId() == Car.CAR_CLIENT_NOT_ASSIGNED_ID) {
			int id = _currentCarClientToConnect.getCarClientId();
			_logger.info(
					"zigbee connection with carClientId: {} connected with car with id: {} ... {} connections left -----------------------",
					new Object[] { id, car.getCarId(), _awaitingConnectionQueue.size() });
			car.setCarClientId(id);
			int oldConnectionCount = _connectionCount;
			_connectionCount++;
			_listenerList.onWorldZigbeeConnectionCountChanged(oldConnectionCount, _connectionCount);
			_assignNextCarClient = true;
			_updateCarInvocationCount.clear();
		}
	}
	
	@Override
	public void onCarClientConnect(ICarClient carClient) {
		try {
			_awaitingConnectionQueue.put(carClient);
			_logger.debug(
					"Currently {} zigbee connections are awaiting a connection to be established.",
					_awaitingConnectionQueue.size());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException("Unexpected Interruption!");
		}
	}
	
	@Override
	public void onCarClientDisconnect(ICarClient carClient) {
		_awaitingConnectionQueue.remove(carClient);
	}
	
	public ListenerList<IWorldZigbeeConnectionCountChanged> getWorldZigbeeConnectionCountListenerList() {
		return _listenerList;
	}
}
