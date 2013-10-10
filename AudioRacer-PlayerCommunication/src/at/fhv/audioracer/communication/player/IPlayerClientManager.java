package at.fhv.audioracer.communication.player;

public interface IPlayerClientManager {
	/**
	 * 
	 * @param playerName
	 * @return playerId
	 */
	public int connect(String playerName);
	
	public void disconnect(int playerId);
	
	public void updateVelocity(int playerId, float speed, float direction);
	
	public boolean selectCar(int playerId, int carId);
	
	/**
	 * 
	 * @param carId
	 * @return Image of the car
	 */
	// TODO: image als byte[]?
	public byte[] getCarImage(int carId);
	
	public void setPlayerReady(int playerId);
}
