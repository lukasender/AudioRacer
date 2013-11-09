package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.core.model.Player;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class PlayerServer extends Server {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerServer.class);
	
	@Override
	protected Connection newConnection() {
		
		_logger.debug("new player connection");
		
		// a connection for each player
		// we do not use RemoteObjects anymore
		PlayerConnection connection = new PlayerConnection(new Player());
		ObjectSpace objectSpace = new ObjectSpace(connection);
		objectSpace.setExecutor(Main.executor); // use the single threaded executor for method invocation
		objectSpace.register(PlayerNetwork.PLAYER_MANAGER, connection); // register the PlayerClientManager to kryonet
		
		return connection;
	}
}
