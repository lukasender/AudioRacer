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
	
	public boolean selectCar(byte carId);
	
	/**
	 * 
	 * @param carId
	 * @return Image of the car
	 */
	public byte[] getCarImage(byte carId);
	
	public void setPlayerReady();
	
	public void trim();
}
