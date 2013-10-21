package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.server.dao.Player;

public class PlayerManager {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerManager.class);
	private static final Map<UUID, Player> _playerList = Collections.synchronizedMap(new HashMap<UUID, Player>());
	
	/**
	 * @param loginName
	 *            - Name of player
	 * @return playerId - Id of the player or -1 if player can not be added
	 */
	public static int addPlayer(UUID uuid, String loginName) {
		Player plr = new Player();
		plr.setLoginName(loginName);
		
		synchronized (_playerList) {
			_playerList.put(uuid, plr);
			_logger.debug("added player {} width playerId {}", loginName, uuid);
		}
		return -1;
	}
	
	/**
	 * @param playerId
	 *            - Id of player to be removed
	 */
	public static void removePlayer(UUID uuid) {
		Player plr = _playerList.remove(uuid);
		if (null != plr) {
			_logger.debug("removed player {} width playerId {}", plr.getLoginName(), uuid);
		}
	}
	
	public static Player getPlayer(UUID uuid) {
		return _playerList.get(uuid);
	}
}
