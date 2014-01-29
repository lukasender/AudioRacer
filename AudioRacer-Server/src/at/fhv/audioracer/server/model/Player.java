package at.fhv.audioracer.server.model;

import at.fhv.audioracer.server.PlayerConnection;

public class Player extends at.fhv.audioracer.core.model.Player {
	
	private PlayerConnection _playerConnection;
	
	public Player() {
		super();
	}
	
	/**
	 * Copy constructor
	 * 
	 * @param toCopy
	 *            Player containing values to copy to this instance
	 */
	public Player(Player toCopy) {
		super();
		_playerId = toCopy.getPlayerId();
		_car = toCopy.getCar();
		_coinsLeft = toCopy.getCoinsLeft();
		_name = toCopy.getName();
		_time = toCopy.getTime();
		_isReady = toCopy.isReady();
		_connectionState = toCopy.getConnectionState();
	}
	
	public void setPlayerConnection(PlayerConnection playerConnection) {
		_playerConnection = playerConnection;
	}
	
	public PlayerConnection getPlayerConnection() {
		return _playerConnection;
	}
	
	public boolean isInGame() {
		return _isReady;
	}
	
	@Override
	public String toString() {
		StringBuilder b = new StringBuilder(super.toString());
		b.append(" kryo-id: ");
		b.append(_playerConnection.getID());
		return b.toString();
	}
}
