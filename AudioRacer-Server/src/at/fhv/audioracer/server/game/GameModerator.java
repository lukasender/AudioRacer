package at.fhv.audioracer.server.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.ConnectResponseMessage;
import at.fhv.audioracer.communication.player.message.FreeCarsMessage;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.server.PlayerConnection;
import at.fhv.audioracer.server.PlayerServer;

public class GameModerator {
	
	private static Logger _logger = LoggerFactory.getLogger(GameModerator.class);
	private PlayerServer _playerServer;
	
	private HashMap<Integer, Player> _playerList = new HashMap<Integer, Player>();
	private int _plrId = 0;
	
	private HashMap<Integer, Car> _carList = new HashMap<Integer, Car>();
	
	public GameModerator(PlayerServer playerServer) {
		_playerServer = playerServer;
	}
	
	/**
	 * called on Player "connect" request
	 * 
	 * @param playerConnection
	 *            the socket connection of this player
	 * @param loginName
	 *            name of player
	 */
	public void connect(PlayerConnection playerConnection, String loginName) {
		Player player = playerConnection.getPlayer();
		player.setLoginName(loginName);
		int id = -1;
		synchronized (_playerList) {
			id = ++_plrId;
			player.setPlayerId(_plrId);
			_playerList.put(_plrId, player);
			_logger.debug("added player {} with playerId {}", loginName, id);
		}
		ConnectResponseMessage resp = new ConnectResponseMessage();
		resp.playerId = id;
		_playerServer.sendToTCP(playerConnection.getID(), resp);
	}
	
	/**
	 * called on Camera "carDetected" request
	 * 
	 * @param newCar
	 *            the detected car
	 */
	public void carDetected(Car newCar) {
		
		if (!_carList.containsKey(newCar.getCarId())) {
			_logger.debug("carDetected - id: {}", newCar.getCarId());
			_carList.put(newCar.getCarId(), newCar);
		} else {
			_logger.warn("Car with id: {} allready known!", newCar.getCarId());
		}
		
		broadcastFreeCars();
	}
	
	public void configureMap(int sizeX, int sizeY) {
		_logger.debug("implement me - configureMap with sizeX: {} and sizeY: {} called", sizeX, sizeY);
	}
	
	public void detectionFinished() {
		_logger.debug("implement me - detectionFinihsed called");
	}
	
	public void updateCar(int carId, float posX, float poxY, float direction) {
		_logger.debug("implement me - updateCar called for carId: {}", carId);
	}
	
	/**
	 * Send currently free cars to all Players.
	 */
	private void broadcastFreeCars() {
		Iterator<Entry<Integer, Car>> it = _carList.entrySet().iterator();
		ArrayList<Integer> freeCars = new ArrayList<Integer>();
		Entry<Integer, Car> entry = null;
		Car car = null;
		while (it.hasNext()) {
			entry = it.next();
			car = entry.getValue();
			if (car.getPlayer() == null) {
				freeCars.add(entry.getKey());
			}
		}
		FreeCarsMessage freeCarsMessage = new FreeCarsMessage();
		int free[] = new int[freeCars.size()];
		for (int i = 0; i < free.length; i++) {
			free[i] = freeCars.get(i).intValue();
		}
		freeCarsMessage.freeCars = free;
		_playerServer.sendToAllTCP(freeCarsMessage);
		int i = 20;
		// TODO: add while(true) force this method to not return
	}
}