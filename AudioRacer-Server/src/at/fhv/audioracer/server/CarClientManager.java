package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarClientManager;
import at.fhv.audioracer.core.util.ListenerList;
import at.fhv.audioracer.server.model.ICarManagerListener;

public class CarClientManager implements ICarClientManager {
	
	private static Logger _logger = LoggerFactory.getLogger(CarClientManager.class);
	
	private static class CarClientListenerList extends ListenerList<ICarManagerListener> implements
			ICarManagerListener {
		
		@Override
		public void onCarClientConnect(ICarClient carClient) {
			for (ICarManagerListener listener : listeners()) {
				listener.onCarClientConnect(carClient);
			}
		}
		
		@Override
		public void onCarClientDisconnect(ICarClient carClient) {
			for (ICarManagerListener listener : listeners()) {
				listener.onCarClientDisconnect(carClient);
			}
		}
		
	}
	
	private CarClientListenerList _listenerList = new CarClientListenerList();
	private Map<Byte, ICarClient> _carClientList = Collections
			.synchronizedMap(new HashMap<Byte, ICarClient>());
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
	public synchronized void connect(ICarClient carClient) {
		_logger.debug("connect ICarClient. id: {}", carClient.getCarClientId());
		_listenerList.onCarClientConnect(carClient);
		_carClientList.put(carClient.getCarClientId(), carClient);
	}
	
	@Override
	public synchronized void disconnect(ICarClient carClient) {
		_logger.debug("disonnect ICarClient. id: {}", carClient.getCarClientId());
		_listenerList.onCarClientDisconnect(carClient);
		_carClientList.remove(carClient.getCarClientId());
	}
	
	public ICarClient get(int carClientId) {
		return _carClientList.get(carClientId);
	}
	
	@Override
	public void invalidCommand() {
		_logger.warn("Invalid command!");
	}
	
	public ListenerList<ICarManagerListener> getCarClientListenerList() {
		return _listenerList;
	}
}
