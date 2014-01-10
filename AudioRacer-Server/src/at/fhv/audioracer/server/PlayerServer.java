package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.server.model.Player;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class PlayerServer extends Server {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerServer.class);
	
	private static PlayerServer _playerServer = null;
	
	private PlayerServer() {
	}
	
	public static PlayerServer getInstance() {
		if (_playerServer == null) {
			_playerServer = new PlayerServer();
		}
		return _playerServer;
	}
	
	@Override
	protected Connection newConnection() {
		
		_logger.debug("new player connection incoming");
		
		Player player = new Player();
		PlayerConnection connection = new PlayerConnection();
		player.setPlayerConnection(connection);
		connection.setPlayer(player);
		return connection;
	}
}
