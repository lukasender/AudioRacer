package at.fhv.audioracer.server.game;

import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.message.ConnectResponseMessage;
import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.server.PlayerConnection;
import at.fhv.audioracer.server.PlayerServer;

public class GameModerator {
	
	private static Logger _logger = LoggerFactory.getLogger(GameModerator.class);
	private PlayerServer _playerServer;
	
	private HashMap<Integer, Player> _playerList = new HashMap<Integer, Player>();
	private int _plrId = 0;
	
	public GameModerator(PlayerServer playerServer) {
		_playerServer = playerServer;
	}
	
	/**
	 * called on Player "connect" request
	 * 
	 * @param playerConnection
	 *            the socket connection of this player
	 * @param loginName
	 *            name of player
	 */
	public void connect(PlayerConnection playerConnection, String loginName) {
		Player player = playerConnection.getPlayer();
		player.setLoginName(loginName);
		int id = -1;
		synchronized (_playerList) {
			id = ++_plrId;
			player.setPlayerId(_plrId);
			_playerList.put(_plrId, player);
			_logger.debug("added player {} with playerId {}", loginName, id);
		}
		ConnectResponseMessage resp = new ConnectResponseMessage();
		resp.playerId = id;
		_playerServer.sendToTCP(playerConnection.getID(), resp);
	}
}
