package at.fhv.audioracer.communication.player;

public interface IPlayerClient extends IClient {
	public void updateCheckpointDirection(float directionX, float directionY);
	
	public void updateFreeCars(int[] carIds);
	
	public void gameOver();
	
	public void invalidCommand();
}
