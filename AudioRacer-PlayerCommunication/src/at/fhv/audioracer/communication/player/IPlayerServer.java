package at.fhv.audioracer.communication.player;

public interface IPlayerServer {
	/**
	 * @param playerClient
	 * @param playerName
	 * @return playerId
	 */
	public int setPlayerName(String playerName);
	
	public void disconnect();
	
	public void updateVelocity(float speed, float direction);
	
	public boolean selectCar(int carId);
	
	/**
	 * 
	 * @param carId
	 * @return Image of the car
	 */
	// TODO: image als byte[]?
	public byte[] getCarImage(int carId);
	
	public void setPlayerReady();
}
