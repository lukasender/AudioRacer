package at.fhv.audioracer.simulator.player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import at.fhv.audioracer.client.player.IPlayerClientListener;
import at.fhv.audioracer.communication.player.IPlayerClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.simulator.utils.Checkpoint;

import com.esotericsoftware.kryonet.Connection;

@Deprecated
public class SimulatedPlayerClient extends Connection implements IPlayerClient {
	
	/**
	 * current direction to send in updateVelocity()
	 */
	protected float _direction;
	
	/**
	 * current accelaration to send in updateVelocity()
	 */
	protected float _accelaration;
	
	/**
	 * List of PlayerClientListener
	 */
	List<IPlayerClientListener> _listener;
	
	/**
	 * holds free cars
	 */
	protected HashMap<Integer, Car> _freeCars;
	
	/**
	 * holds every known car
	 */
	protected HashMap<Integer, Car> _cars;
	
	/**
	 * holds connected players
	 */
	protected HashMap<Integer, Player> _players;
	
	/**
	 * player of this client
	 */
	protected Player _player;
	
	/**
	 * playerId of this client
	 */
	protected int _playerId;
	
	/**
	 * coins this player has to collect before game ends
	 */
	protected int _coinsLeft;
	
	/**
	 * time in ms since game start
	 */
	protected int _time;
	
	/**
	 * next relative checkpoint
	 */
	protected Checkpoint _nextCheckpoint;
	
	public SimulatedPlayerClient() {
		
		_cars = new HashMap<Integer, Car>();
		_freeCars = new HashMap<Integer, Car>();
		_player = new Player();
		_nextCheckpoint = new Checkpoint();
		_listener = new LinkedList<>();
	}
	
	@Override
	public void updateGameState(int playerId, int coinsLeft, int time) {
		Player p = _players.get(playerId);
		p.setCoinsLeft(coinsLeft);
		p.setTime(time);
		
	}
	
	@Override
	public void playerConnected(int playerId, String playerName) {
		if (_players != null && !_players.containsKey(playerId)) {
			Player p = new Player();
			p.setPlayerId(playerId);
			p.setLoginName(playerName);
		}
		
	}
	
	@Override
	public void playerDisconnected(int playerId) {
		if (_players.containsKey(playerId)) {
			_players.remove(playerId);
		}
		
	}
	
	@Override
	public void updateCheckpointDirection(float directionX, float directionY) {
		
		_nextCheckpoint.setXDistance(directionX);
		_nextCheckpoint.setYDistance(directionY);
		
	}
	
	@Override
	public void updateFreeCars(int[] carIds) {
		
		for (int currId : carIds) {
			if (_cars.containsKey(currId)) {
				_freeCars.put(currId, _cars.get(currId));
			} else {
				_cars.put(currId, new Car(currId));
				_freeCars.put(currId, _cars.get(currId));
			}
		}
		
	}
	
	@Override
	public void gameOver() {
		
	}
	
	@Override
	public void invalidCommand() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @return current value direction which is sent to PlayerClientManager.updateVelocity
	 */
	public float getDirection() {
		return _direction;
	}
	
	/**
	 * 
	 * @param direction
	 *            change value of direction which is sent to PlayerClientManager.updateVelocity
	 */
	public void setDirection(float direction) {
		_direction = direction;
	}
	
	/**
	 * 
	 * @return current value accelaration which is sent to PlayerClientManager.updateVelocity
	 */
	public float getAccelaration() {
		return _accelaration;
	}
	
	/**
	 * 
	 * @param accelaration
	 *            change value of accelaration which is sent to PlayerClientManager.updateVelocity
	 */
	public void setAccelaration(float accelaration) {
		_accelaration = accelaration;
	}
	
	@Override
	public void gameStarts() {
		// TODO Auto-generated method stub
		
	}
	
}
