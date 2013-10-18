package at.fhv.audioracer.communication.world;

public interface ICarClientManager {
	public void connect();
	
	public void disconnect();
	
	public void invalidCommand();
}
