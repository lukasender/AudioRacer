package at.fhv.audioracer.server;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.IPlayerClient;
import at.fhv.audioracer.communication.player.IPlayerClientManager;
import at.fhv.audioracer.server.dao.Player;
import at.fhv.audioracer.server.wrappers.GameUnit;

import com.esotericsoftware.kryonet.Connection;

public class PlayerClientManager extends Connection implements IPlayerClientManager {
	
	private static final Logger _logger = LoggerFactory.getLogger(PlayerClientManager.class);
	private final Map<Integer, GameUnit> _playerList = Collections.synchronizedMap(new HashMap<Integer, GameUnit>());
	private int _playerCtr = 0;
	
	private IPlayerClient _playerClient;
	
	public IPlayerClient getPlayerClient() {
		return _playerClient;
	}
	
	public void setPlayerClient(IPlayerClient playerClient) {
		_playerClient = playerClient;
	}
	
	/**
	 * @param loginName
	 *            - Name of player
	 * @return playerId - Id of the player or -1 if player can not be added
	 */
	public int addPlayer(String loginName) {
		Integer plrId = null;
		Player plr = new Player();
		plr.setLoginName(loginName);
		
		GameUnit gUnit = new GameUnit();
		gUnit.setPlayer(plr);
		
		synchronized (_playerList) {
			_playerCtr++;
			_playerList.put(_playerCtr, gUnit);
			_logger.debug("added player {} width playerId {}", loginName, plrId);
		}
		return plrId == null ? -1 : plrId.intValue();
	}
	
	/**
	 * @param playerId
	 *            - Id of player to be removed
	 */
	public void removePlayer(int playerId) {
		GameUnit gUnit = _playerList.remove(new Integer(playerId));
		if (null != gUnit) {
			// TODO: free car
			_logger.debug("removed player {} width playerId {}", gUnit.getPlayer().getLoginName(), playerId);
		}
	}
	
	/**
	 * @param playerId
	 *            - The playerId
	 * @return The GameUnit player is part of or null if player does not exist
	 */
	public GameUnit getGameUnit(int playerId) {
		return _playerList.get(new Integer(playerId));
	}
	
	@Override
	public int connect(String playerName) {
		_logger.debug("hello {}", playerName);
		
		_playerClient.invalidCommand();
		return 47;
	}
	
	@Override
	public void disconnect() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean selectCar(int carId) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public byte[] getCarImage(int carId) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void setPlayerReady() {
		// TODO Auto-generated method stub
		
	}
}
