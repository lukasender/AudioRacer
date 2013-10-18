package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.server.dao.Player;
import at.fhv.audioracer.server.wrappers.GameUnit;

public class PlayerManager {
	
	private Logger logger = LoggerFactory.getLogger(PlayerManager.class);
	private final Map<Integer, GameUnit> playerList = Collections.synchronizedMap(new HashMap<Integer, GameUnit>());
	private int playerCtr = 0;
		
	/**
	 * @param loginName - Name of player
	 * @return playerId - Id of the player or -1 if player can not be added
	 */
	public int addPlayer(String loginName) {
		Integer plrId = null;
		Player plr = new Player();
		plr.setLoginName(loginName);
		
		GameUnit gUnit = new GameUnit();
		gUnit.setPlayer(plr);
		
		synchronized (playerList) {
			playerCtr++;
			playerList.put(playerCtr, gUnit);
			logger.debug("added player {} width playerId {}", loginName, plrId);
		}
		return plrId == null ? -1 : plrId.intValue();
	}
	
	/**
	 * @param playerId - Id of player to be removed
	 */
	public void removePlayer(int playerId) {
		GameUnit gUnit = playerList.remove(new Integer(playerId));
		if(null != gUnit) {
			//TODO: free car
			logger.debug("removed player {} width playerId {}", gUnit.getPlayer().getLoginName(), playerId);
		}
	}
	
	/**
	 * @param playerId - The playerId
	 * @return The GameUnit player is part of or null if player does not exist
	 */
	public GameUnit getGameUnit(int playerId) {
		return playerList.get(new Integer(playerId));
	}
}
