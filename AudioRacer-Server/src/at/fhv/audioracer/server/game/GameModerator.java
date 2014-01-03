package at.fhv.audioracer.server.game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.FreeCarsMessage;
import at.fhv.audioracer.communication.player.message.PlayerConnectedMessage;
import at.fhv.audioracer.communication.player.message.PlayerDisconnectedMessage;
import at.fhv.audioracer.communication.player.message.SelectCarResponseMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameResponseMessage;
import at.fhv.audioracer.communication.player.message.StartGameMessage;
import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.server.CarClientManager;
import at.fhv.audioracer.server.PlayerConnection;
import at.fhv.audioracer.server.PlayerServer;
import at.fhv.audioracer.server.WorldZigbeeMediator;
import at.fhv.audioracer.server.model.ICarManagerListener;
import at.fhv.audioracer.server.model.IWorldZigbeeConnectionCountChanged;

public class GameModerator implements ICarManagerListener, IWorldZigbeeConnectionCountChanged {
	
	private static Logger _logger = LoggerFactory.getLogger(GameModerator.class);
	private PlayerServer _playerServer;
	private CheckpointUtil _checkpointUtil = new CheckpointUtil();
	private int _checkpointStartCount = 5;
	
	private HashMap<Integer, Player> _playerList = new HashMap<Integer, Player>();
	private int _plrId = 0;
	
	private Map<Byte, Car> _carList = Collections.synchronizedMap(new HashMap<Byte, Car>());
	private Map<Byte, ArrayDeque<Position>> _checkpoints = Collections
			.synchronizedMap(new HashMap<Byte, ArrayDeque<Position>>());
	
	private Thread _worldZigbeeThread = null;
	private WorldZigbeeMediator _worldZigbeeRunnable = new WorldZigbeeMediator();
	
	private Object _lockObject = new Object();
	private boolean _gameRunning = false;
	
	// next conditions must be true for game start
	private boolean _mapConfigured = false;
	private boolean _detectionFinished = false;
	
	private at.fhv.audioracer.core.model.Map _map = null;
	private static GameModerator _gameModerator = null;
	
	private GameModerator() {
		_playerServer = PlayerServer.getInstance();
		CarClientManager.getInstance().getCarClientListenerList().add(this);
		_worldZigbeeThread = new Thread(_worldZigbeeRunnable);
		CarClientManager.getInstance().getCarClientListenerList().add(_worldZigbeeRunnable);
	}
	
	public static GameModerator getInstance() {
		if (_gameModerator == null) {
			_gameModerator = new GameModerator();
		}
		return _gameModerator;
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
		
		// TODO: player can set his name twice, this should probably not allowed
		
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
		_broadcastFreeCars();
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
				_logger.warn("carDetected not allowed!"
						+ " Game running: {}, car detection finished: {}", _gameRunning,
						_detectionFinished);
			} else {
				if (!_carList.containsKey(newCar.getCarId())) {
					_logger.debug("carDetected - id: {}", newCar.getCarId());
					
					_carList.put(newCar.getCarId(), newCar);
					_checkpoints.put(newCar.getCarId(), new ArrayDeque<Position>());
					if (_map != null) {
						_map.addCar(newCar);
					}
					newCar.getCarListenerList().add(_worldZigbeeRunnable);
				} else {
					_logger.warn("Car with id: {} allready known!", newCar.getCarId());
				}
			}
		}
		
		_broadcastFreeCars();
	}
	
	public void configureMap(int sizeX, int sizeY) {
		_logger.debug("configureMap with sizeX: {} and sizeY: {} called", sizeX, sizeY);
		
		synchronized (_lockObject) {
			if (_gameRunning) {
				_logger.warn("configureMap not allowed while game is running!");
			} else {
				_mapConfigured = true;
				_checkpointUtil.setMapSize(sizeX, sizeY);
				if (_map != null) {
					_map.setSizeX(sizeX);
					_map.setSizeY(sizeY);
				}
				_checkPreconditionsAndStartGameIfAllFine();
			}
		}
	}
	
	public void detectionFinished() {
		_logger.debug("detectionFinished called");
		
		synchronized (_lockObject) {
			
			if (_gameRunning) {
				_logger.warn("detectionFinished cannot be called during game is running!");
			} else if (_carList.size() > 0) {
				_detectionFinished = true;
				
				// camera detection of cars finished, connect ZigBee within a background thread and let
				// this method return instantly so kryonet listener can continue to do its work
				if (_worldZigbeeThread.isAlive() == false) {
					_worldZigbeeThread.start();
				}
				_checkPreconditionsAndStartGameIfAllFine();
			} else {
				_logger.warn("server will not accept detection finished "
						+ "upon at least one car ist detected!");
			}
		}
	}
	
	public void updateCar(byte carId, float posX, float posY, float direction) {
		// _logger.debug(
		// "updateCar called for carId: {} game started: {} posX: {} posY: {} direction: {}",
		// new Object[] { carId, _gameRunning, posX, posY, direction });
		Car car = _carList.get(carId);
		if (_gameRunning) {
			car.updatePosition(new Position(posX, posY), new Direction(direction));
		} else {
			car.updatePosition(new Position(posX, posY), new Direction(direction));
		}
	}
	
	public void selectCar(PlayerConnection playerConnection, byte carId) {
		_logger.debug("selectCar called from player with id: {} and name: {}", playerConnection
				.getPlayer().getPlayerId(), playerConnection.getPlayer().getName());
		
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
							_logger.warn("car with id: {} allready owned by: {}", carId,
									player.getName());
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
			
			_broadcastFreeCars();
		}
	}
	
	/**
	 * Send currently free cars to all Players.
	 */
	private void _broadcastFreeCars() {
		Iterator<Entry<Byte, Car>> it = _carList.entrySet().iterator();
		ArrayList<Byte> freeCars = new ArrayList<Byte>();
		Entry<Byte, Car> entry = null;
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
		byte free[] = new byte[freeCars.size()];
		for (int i = 0; i < free.length; i++) {
			free[i] = freeCars.get(i).byteValue();
		}
		freeCarsMessage.freeCars = free;
		_playerServer.sendToAllTCP(freeCarsMessage);
	}
	
	private void _checkPreconditionsAndStartGameIfAllFine() {
		
		if (_gameRunning == false && _mapConfigured && _detectionFinished) {
			
			// check all cars available have a player connected (=selectCar)
			// and this players are all in ready state (=setPlayerReady)
			// at this state we have at least one Car in _carList
			Iterator<Entry<Byte, Car>> it = _carList.entrySet().iterator();
			Entry<Byte, Car> entry = null;
			Car car = null;
			Car cars[] = new Car[_carList.size()];
			int carsCount = -1;
			try {
				while (it.hasNext()) {
					entry = it.next();
					car = entry.getValue();
					carsCount++;
					cars[carsCount] = car;
					if (car.getPlayer() == null) {
						return;
					} else if (!car.getPlayer().isReady()) {
						return;
					}
				}
			} catch (ConcurrentModificationException e) {
				return;
			}
			
			// check that as much zigBee connections are established as cars available
			if (_worldZigbeeRunnable.getConnectionCount() != _carList.size()) {
				_logger.info(
						"Expected zigBeeConnection count {} currently not fulfill expectation count {} ",
						_worldZigbeeRunnable.getConnectionCount(), _carList.size());
				return;
			}
			
			_logger.info("Game preconditions are all given.");
			_logger.info("Generate checkpoints ....");
			
			float randomVectorLength = 0;
			Position previousCheckpoint = null;
			for (int i = 0; i < _checkpointStartCount; i++) {
				randomVectorLength = _checkpointUtil.generateRandomVectorLength();
				for (int y = 0; y < cars.length; y++) {
					car = cars[y];
					if (i == 0) {
						previousCheckpoint = car.getPosition();
					} else {
						previousCheckpoint = _checkpoints.get(car.getCarId()).getFirst();
					}
					_logger.debug("generate checkpoint number: {} for carId: {}", i, car.getCarId());
					_checkpoints.get(car.getCarId()).addLast(
							_checkpointUtil.generateNextCheckpoint(previousCheckpoint,
									randomVectorLength));
				}
			}
			
			_logger.info("Checkpoints generated ....");
			_logger.info("Game will start now.");
			
			_gameRunning = true;
			
			if (_worldZigbeeThread.isAlive()) {
				_worldZigbeeThread.interrupt();
			}
			
			// TODO: good idea to send in synchronized block?
			StartGameMessage startGameMsg = new StartGameMessage();
			startGameMsg.gameWillStartInMilliseconds = 0;
			_playerServer.sendToAllTCP(startGameMsg);
		}
	}
	
	/**
	 * @param playerConnection
	 *            Socket connection of player who send request of type PlayerMessage with PlayerMessage.MessageId = DISCONNECT
	 */
	public void disconnectPlayer(PlayerConnection playerConnection) {
		boolean playerHasBeenDecoubled = false;
		synchronized (_lockObject) {
			if (playerConnection.getPlayer().getCar() != null) {
				// decouple car and player instance
				Car car = playerConnection.getPlayer().getCar();
				playerConnection.getPlayer().setCar(null);
				car.setPlayer(null);
				playerHasBeenDecoubled = true;
			}
		}
		if (playerHasBeenDecoubled) {
			PlayerDisconnectedMessage playerDisconnectedMsg = new PlayerDisconnectedMessage();
			playerDisconnectedMsg.playerId = playerConnection.getPlayer().getPlayerId();
			_playerServer.sendToAllExceptTCP(playerConnection.getID(), playerDisconnectedMsg);
			_broadcastFreeCars();
		}
	}
	
	public void setPlayerReady(PlayerConnection playerConnection) {
		
		synchronized (_lockObject) {
			Player player = playerConnection.getPlayer();
			player.setReady(true);
			
			_logger.debug("Player {} with id {} in ready state.", player.getName(),
					player.getPlayerId());
			
			_checkPreconditionsAndStartGameIfAllFine();
		}
	}
	
	@Override
	public void onCarClientConnect(ICarClient carClient) {
		// TODO: if game has paused while running, return to game if all connections established again
	}
	
	@Override
	public void onCarClientDisconnect(ICarClient carClient) {
		// TODO: if game is running, remember carClientID(s) and pause game
	}
	
	@Override
	public void onWorldZigbeeConnectionCountChanged(int oldValue, int newValue) {
		synchronized (_lockObject) {
			_checkPreconditionsAndStartGameIfAllFine();
		}
	}
	
	public void updateVelocity(PlayerConnection playerConnection, float speed, float direction) {
		if (_detectionFinished == false)
			return; // suppress user interaction until camera finished car detection
			
		ICarClient c = CarClientManager.getInstance().get(
				playerConnection.getPlayer().getCar().getCarClientId());
		if (c != null) {
			c.updateVelocity(speed, direction);
		} else {
			_logger.warn("ICarClient for carId: {} is null!", playerConnection.getPlayer().getCar()
					.getCarId());
		}
	}
	
	public void setMap(at.fhv.audioracer.core.model.Map map) {
		_map = map;
	}
}