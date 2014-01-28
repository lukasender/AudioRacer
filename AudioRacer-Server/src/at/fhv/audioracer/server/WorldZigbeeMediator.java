package at.fhv.audioracer.server;

import java.util.HashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarManagerListener;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.ICarListener;
import at.fhv.audioracer.core.util.ListenerList;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.server.game.CheckpointUtil;
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
	
	// Object[0] = Car
	// Object[1] = initial Position, detection process started with
	private HashMap<Byte, Object[]> _carAndInitialPositionMap = new HashMap<>();
	
	private int _connectionCount = 0;
	private float _configurationSpeed = 0.2f;
	private float _configurationDirection = 0.0f;
	private WorldZigbeeConnectionCountListenerList _listenerList = new WorldZigbeeConnectionCountListenerList();
	private int _mapX = 0;
	private int _mapY = 0;
	private float _distanceToDrive = 0.f;
	private CheckpointUtil _rangeUtil = new CheckpointUtil();
	
	@Override
	public void run() {
		_logger.info("Mediator thread start.");
		
		try {
			while (true) {
				if (_assignNextCarClient) {
					_assignNextCarClient = false;
					_currentCarClientToConnect = _awaitingConnectionQueue.take();
				} else {
					Thread.sleep(10);
					// _logger.debug("try to assign CarClient ... send update velocity.");
					_currentCarClientToConnect.updateVelocity(_configurationSpeed,
							_configurationDirection);
					
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
	public void onCarPositionChanged(Car<?> car) {
		byte carId = car.getCarId();
		
		// return immediately on Car movement misinterpretation from camera implementation
		if (car.getCarClientId() != Car.CAR_CLIENT_NOT_ASSIGNED_ID) {
			return;
		}
		
		if (!_carAndInitialPositionMap.containsKey(carId)) {
			_carAndInitialPositionMap.put(carId, new Object[2]);
		}
		Object[] carPosMap = _carAndInitialPositionMap.get(carId);
		if (carPosMap[1] == null) {
			carPosMap[1] = car.getPosition();
			_logger.debug("Initial car position of car-id: {} is {}", car.getCarId(),
					car.getPosition());
		}
		float distToInitialPosition = _rangeUtil.getDistance(car.getPosition(),
				(Position) carPosMap[1]);
		if (distToInitialPosition > _distanceToDrive) {
			_logger.debug("Car with id {} drove {} ... it will be paired with Car-client-id: {}",
					car.getCarId(), distToInitialPosition,
					_currentCarClientToConnect.getCarClientId());
			car.setCarClientId(_currentCarClientToConnect.getCarClientId());
			int oldConnectionCount = _connectionCount;
			_connectionCount++;
			_listenerList.onWorldZigbeeConnectionCountChanged(oldConnectionCount, _connectionCount);
			_assignNextCarClient = true;
			_carAndInitialPositionMap.remove(carId);
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
	
	public void setMapSize(int sizeX, int sizeY) {
		_mapX = sizeX;
		_mapY = sizeY;
		_distanceToDrive = (Math.min(_mapX, _mapY) / 4);
		_logger.debug("Each Car must drive {} to be detected.", _distanceToDrive);
	}
}
