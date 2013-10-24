package at.fhv.audioracer.simulator.player;

import java.util.HashMap;

import at.fhv.audioracer.communication.player.IPlayerClient;
import at.fhv.audioracer.core.model.Player;

import com.esotericsoftware.kryonet.Connection;

public class SimulatedPlayerClient extends Connection implements IPlayerClient {
	
	private HashMap<Integer, byte[]> _freeCars;
	private HashMap<Integer, Player> _players;
	private Player _player;
	private int _playerId;
	private int _coinsLeft;
	private int _time;
	
	@Override
	public void updateGameState(int playerId, int coinsLeft, int time) {
		Player p = _players.get(playerId);
		p.setCoinsLeft(coinsLeft);
		p.setTime(time);
		
		// TODO notify gui
		
	}
	
	@Override
	public void playerConnected(int playerId, String playerName) {
		// TODO Auto-generated method stub
		if (_players != null && !_players.containsKey(playerId)) {
			Player p = new Player();
			p.setPlayerId(playerId);
			p.setLoginName(playerName);
		}
	}
	
	@Override
	public void playerDisconnected(int playerId) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateCheckpointDirection(float directionX, float directionY) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void updateFreeCars(int[] carIds) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void invalidCommand() {
		// TODO Auto-generated method stub
		
	}
	
}
