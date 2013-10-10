package at.fhv.audioracer.communication.player;

public interface IClient {
	/**
	 * 
	 * @param playerId
	 * @param coinsLeft
	 * @param time in milliseconds
	 */
	// TODO: time als int?
	public void updateGameState(int playerId, int coinsLeft, int time);
	
	public void playerConnected(int playerId, String playerName);
	
	public void playerDisconnected(int playerId);
}
