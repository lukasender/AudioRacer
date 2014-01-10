package at.fhv.audioracer.server.model;

import at.fhv.audioracer.server.PlayerConnection;

public class Player extends at.fhv.audioracer.core.model.Player {
	
	private PlayerConnection _playerConnection;
	
	public Player() {
		super();
	}
	
	public void setPlayerConnection(PlayerConnection playerConnection) {
		_playerConnection = playerConnection;
	}
	
	public PlayerConnection getPlayerConnection() {
		return _playerConnection;
	}
}
