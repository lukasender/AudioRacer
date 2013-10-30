package at.fhv.audioracer.communication.player;

public interface IClient {
	/**
	 * @param 	playerId 	id of player game state belongs to
	 * @param 	coinsLeft 	coins left count of this player
	 * @param 	time 		server time (milliseconds) game stated was recorded at
	 */
	public void updateGameState(int playerId, int coinsLeft, int time);
	
	public void playerConnected(int playerId, String playerName);
	
	public void playerDisconnected(int playerId);
}
