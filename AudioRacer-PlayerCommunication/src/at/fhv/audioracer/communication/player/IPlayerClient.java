package at.fhv.audioracer.communication.player;

public interface IPlayerClient extends IClient {
	public void updateCheckpointDirection(float directionX, float directionY);
	
	public void updateFreeCars(byte[] carIds);
	
	public void gameOver();
	
	public void invalidCommand();
	
	public void gameStarts();
}
