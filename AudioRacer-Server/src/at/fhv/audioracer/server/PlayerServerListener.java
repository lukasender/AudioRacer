package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.SetPlayerNameRequestMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage;
import at.fhv.audioracer.communication.player.message.SelectCarRequestMessage;
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
		if (object instanceof PlayerMessage) {
			PlayerMessage message = (PlayerMessage) object;
			PlayerConnection playerConnection = (PlayerConnection) connection;
			switch (message.messageId) {
				case SET_PLAYER_NAME_REQUEST:
					SetPlayerNameRequestMessage setNameReqMsg = (SetPlayerNameRequestMessage) message;
					_gameModerator.setPlayerName(playerConnection, setNameReqMsg.playerName);
					break;
				case SELECT_CAR_REQUEST:
					SelectCarRequestMessage selectCarReqMsg = (SelectCarRequestMessage) message;
					_gameModerator.selectCar(playerConnection, selectCarReqMsg.carId);
				default:
					_logger.warn("Message with id: {} not known!", message.messageId);
			}
		}
	}
	
	public void disconnected(Connection connection) {
		PlayerConnection playerConnection = (PlayerConnection) connection;
		_logger.debug("player connection for player with id: {} has been closed.", playerConnection.getPlayer().getPlayerId());
	}
}
