package at.fhv.audioracer.server;

import at.fhv.audioracer.core.model.Player;

import com.esotericsoftware.kryonet.Connection;

public class PlayerConnection extends Connection {
	Player _player;
	
	public PlayerConnection(Player player) {
		_player = player;
	}
	
	public Player getPlayer() {
		return _player;
	}
}
