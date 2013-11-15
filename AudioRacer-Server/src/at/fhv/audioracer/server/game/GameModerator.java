package at.fhv.audioracer.server.game;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.FreeCarsMessage;
import at.fhv.audioracer.communication.player.message.PlayerConnectedMessage;
import at.fhv.audioracer.communication.player.message.SelectCarResponseMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameResponseMessage;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.server.PlayerConnection;
import at.fhv.audioracer.server.PlayerServer;

public class GameModerator {
	
	private static Logger _logger = LoggerFactory.getLogger(GameModerator.class);
	private PlayerServer _playerServer;
	private Ranking _ranking;
	
	private HashMap<Integer, Player> _playerList = new HashMap<Integer, Player>();
	private int _plrId = 0;
	
	private HashMap<Integer, Car> _carList = new HashMap<Integer, Car>();
	
	private Object _lockObject = new Object();
	private boolean _gameRunning = false;
	
	// next conditions must be true for game start
	private boolean _mapConfigured = false;
	private boolean _detectionFinished = false;
	private boolean _allZigBeeConnected = false;
	
	public GameModerator(PlayerServer playerServer) {
		_playerServer = playerServer;
	}
	
	/**
	 * called on Player tries to set his player name
	 * 
	 * @param playerConnection
	 *            the socket connection of this player
	 * @param playerName
	 *            name of player
	 */
	public void setPlayerName(PlayerConnection playerConnection, String playerName) {
		int id = -1;
		if (playerName != null) {
			Player player = playerConnection.getPlayer();
			player.setName(playerName);
			synchronized (_playerList) {
				id = ++_plrId;
				player.setPlayerId(_plrId);
				_playerList.put(_plrId, player);
				_logger.debug("added player {} with playerId {}", playerName, id);
			}
		} else {
			_logger.warn("player name received is null! This is not allowed!");
		}
		SetPlayerNameResponseMessage resp = new SetPlayerNameResponseMessage();
		resp.playerId = id;
		_playerServer.sendToTCP(playerConnection.getID(), resp);
		broadcastFreeCars();
	}
	
	/**
	 * called on Camera "carDetected" request
	 * 
	 * @param newCar
	 *            the detected car
	 */
	public void carDetected(Car newCar) {
		
		synchronized (_lockObject) {
			if (_gameRunning || _detectionFinished) {
				_logger.warn("carDetected not allowed!" + " Game running: {}, car detection finished: {}", _gameRunning, _detectionFinished);
			} else {
				if (!_carList.containsKey(newCar.getCarId())) {
					_logger.debug("carDetected - id: {}", newCar.getCarId());
					_allZigBeeConnected = false;
					_carList.put(newCar.getCarId(), newCar);
				} else {
					_logger.warn("Car with id: {} allready known!", newCar.getCarId());
				}
			}
		}
		
		broadcastFreeCars();
	}
	
	public void configureMap(int sizeX, int sizeY) {
		_logger.debug("configureMap with sizeX: {} and sizeY: {} called", sizeX, sizeY);
		
		synchronized (_lockObject) {
			if (_gameRunning) {
				_logger.warn("configureMap not allowed while game is running!");
			} else {
				_ranking = new Ranking(sizeX, sizeY);
				_mapConfigured = true;
			}
		}
	}
	
	public void detectionFinished() {
		_logger.debug("detectionFinihsed called");
		
		synchronized (_lockObject) {
			_detectionFinished = true;
		}
	}
	
	public void updateCar(int carId, float posX, float poxY, float direction) {
		_logger.debug("implement me - updateCar called for carId: {}", carId);
	}
	
	public void selectCar(PlayerConnection playerConnection, int carId) {
		_logger.debug("selectCar called from player with id: {} and name: {}", playerConnection.getPlayer().getPlayerId(), playerConnection.getPlayer()
				.getName());
		
		SelectCarResponseMessage selectResponse = new SelectCarResponseMessage();
		selectResponse.successfull = false;
		
		if (playerConnection.getPlayer().getName() == null) {
			_logger.warn("selectCar - player has to set player name first before selecting a car!");
		} else {
			synchronized (_lockObject) {
				if (_gameRunning) {
					_logger.warn("selectCar not allowed while game is running!");
				} else {
					if (_carList.containsKey(carId) && _carList.get(carId).getPlayer() == null) {
						Car carToSelect = _carList.get(carId);
						carToSelect.setPlayer(playerConnection.getPlayer());
						playerConnection.getPlayer().setCar(carToSelect);
						selectResponse.successfull = true;
					} else {
						// for development purposes only
						if (_carList.containsKey(carId) == false) {
							_logger.warn("car with id: {} doesn't exist!", carId);
						} else {
							Player player = _carList.get(carId).getPlayer();
							_logger.warn("car with id: {} allready owned by: {}", carId, player.getName());
						}
					}
				}
			}
		}
		
		_playerServer.sendToTCP(playerConnection.getID(), selectResponse);
		
		if (selectResponse.successfull) {
			
			PlayerConnectedMessage plrConnectedMsg = new PlayerConnectedMessage();
			plrConnectedMsg.id = playerConnection.getPlayer().getPlayerId();
			plrConnectedMsg.playerName = playerConnection.getPlayer().getName();
			_playerServer.sendToAllExceptTCP(playerConnection.getID(), plrConnectedMsg);
			
			broadcastFreeCars();
		}
	}
	
	/**
	 * Send currently free cars to all Players.
	 */
	private void broadcastFreeCars() {
		Iterator<Entry<Integer, Car>> it = _carList.entrySet().iterator();
		ArrayList<Integer> freeCars = new ArrayList<Integer>();
		Entry<Integer, Car> entry = null;
		Car car = null;
		try {
			while (it.hasNext()) {
				entry = it.next();
				car = entry.getValue();
				if (car.getPlayer() == null) {
					freeCars.add(entry.getKey());
				}
			}
		} catch (ConcurrentModificationException e) {
			// _carList has changed, next broad cast will come fore sure
			// don't care
			_logger.warn("ConcurrentModificationException caught in broadcastFreeCars!", e);
			return;
		}
		
		FreeCarsMessage freeCarsMessage = new FreeCarsMessage();
		int free[] = new int[freeCars.size()];
		for (int i = 0; i < free.length; i++) {
			free[i] = freeCars.get(i).intValue();
		}
		freeCarsMessage.freeCars = free;
		_playerServer.sendToAllTCP(freeCarsMessage);
	}
}