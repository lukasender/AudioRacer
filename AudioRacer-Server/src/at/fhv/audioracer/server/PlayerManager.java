package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.server.dao.PlayerProxy;

public class PlayerManager {
	
	private static Logger _logger = LoggerFactory.getLogger(PlayerManager.class);
	private static final Map<Integer, PlayerProxy> _playerList = Collections.synchronizedMap(new HashMap<Integer, PlayerProxy>());
	private static int _plrId = 0;
	
	/**
	 * @param loginName
	 *            name of player
	 * @return id of the player or -1 if player can not be added
	 */
	public static int addPlayer(String loginName) {
		PlayerProxy plr = new PlayerProxy();
		plr.setLoginName(loginName);
		int id = -1;
		synchronized (_playerList) {
			id = ++_plrId;
			_playerList.put(_plrId, plr);
			_logger.debug("added player {} with playerId {}", loginName, id);
		}
		return id;
	}
	
	/**
	 * @param playerId
	 *            Id of player to be removed
	 */
	public static void removePlayer(int playerId) {
		PlayerProxy plr = _playerList.remove(playerId);
		if (null != plr) {
			_logger.debug("removed player {} width playerId {}", plr.getLoginName(), playerId);
		}
	}
	
	/**
	 * @param playerId
	 *            ID of player requested
	 * @return PlayerProxy associated with this id
	 */
	public static PlayerProxy getPlayer(int playerId) {
		return _playerList.get(playerId);
	}
}
