package at.fhv.audioracer.communication.world;

public interface ICarClientManager {
	public void connect(ICarClient car);
	
	public void disconnect(ICarClient car);
	
	public void invalidCommand();
}
