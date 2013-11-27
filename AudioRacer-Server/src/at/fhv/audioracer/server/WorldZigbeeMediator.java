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
import at.fhv.audioracer.server.model.ICarClientListener;
import at.fhv.audioracer.server.model.IWorldZigbeeConnectionCountChanged;

public class WorldZigbeeMediator implements Runnable, ICarListener, ICarClientListener {
	
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
	private HashMap<Integer, Integer> _updateCarInvocationCount = new HashMap<Integer, Integer>();
	private final int _upateCarInvocationCountThreshold = 300;
	private int _connectionCount = 0;
	private float _configurationSpeed = 0.1f;
	private float _configurationDirection = 0.0f;
	private WorldZigbeeConnectionCountListenerList _listenerList = new WorldZigbeeConnectionCountListenerList();
	
	@Override
	public void run() {
		_logger.info("Mediator thread start.");
		
		try {
			while (true) {
				_currentCarClientToConnect = _awaitingConnectionQueue.take();
				float speed = _configurationSpeed;
				while (_currentCarClientToConnect != null) {
					speed *= -1.0f;
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
		int carId = car.getCarId();
		if (_updateCarInvocationCount.containsKey(carId) == false) {
			count = 1;
			_updateCarInvocationCount.put(carId, count);
		} else {
			count = _updateCarInvocationCount.get(carId);
			count++;
			_updateCarInvocationCount.put(car.getCarId(), count);
		}
		
		if (count > _upateCarInvocationCountThreshold) {
			int id = _currentCarClientToConnect.getCarClientId();
			_logger.info(
					"zigbee connection with carClientId: {} connected with car with id: {} ... {} connections left",
					new Object[] { id, car.getCarId(), _awaitingConnectionQueue.size() });
			car.setCarClientId(id);
			int oldConnectionCount = _connectionCount;
			_connectionCount++;
			_listenerList.onWorldZigbeeConnectionCountChanged(oldConnectionCount, _connectionCount);
			_currentCarClientToConnect = null; // clear for next
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
