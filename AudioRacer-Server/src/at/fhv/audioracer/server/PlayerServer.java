package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.core.model.Player;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class PlayerServer extends Server {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerServer.class);
	
	@Override
	protected Connection newConnection() {
		
		_logger.debug("new player connection incoming");
		
		// a connection for each player
		// we do not use RemoteObjects anymore
		// attach the player to his connection
		PlayerConnection connection = new PlayerConnection(new Player());
		return connection;
	}
}
