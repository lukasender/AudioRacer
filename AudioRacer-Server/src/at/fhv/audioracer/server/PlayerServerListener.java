package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.ConnectRequestMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage;
import at.fhv.audioracer.server.game.GameModerator;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PlayerServerListener extends Listener {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerServerListener.class);
	private GameModerator _gameModerator = null;
	
	public PlayerServerListener(GameModerator gameModerator) {
		_gameModerator = gameModerator;
	}
	
	public void received(Connection connection, Object object) {
		PlayerMessage message = (PlayerMessage) object;
		PlayerConnection playerConnection = (PlayerConnection) connection;
		switch (message.messageId) {
			case CONNECT_REQUEST:
				ConnectRequestMessage msg = (ConnectRequestMessage) message;
				_gameModerator.connect(playerConnection, msg.playerName);
				break;
			default:
				_logger.warn("Message with id: {} not known!", message.messageId);
		}
	}
	
	public void disconnected(Connection connection) {
		PlayerConnection playerConnection = (PlayerConnection) connection;
		_logger.debug("player connection for player with id: {} has been closed.", playerConnection.getPlayer().getPlayerId());
	}
}
