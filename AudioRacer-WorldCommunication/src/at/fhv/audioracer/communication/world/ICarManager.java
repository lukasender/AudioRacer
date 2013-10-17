package at.fhv.audioracer.communication.world;

public interface ICarManager {
	public void connect(int zigbeeId);
	
	public void disconnect(int zigbeeId);
	
	public void invalidCommand(int zigbeeId);
}
