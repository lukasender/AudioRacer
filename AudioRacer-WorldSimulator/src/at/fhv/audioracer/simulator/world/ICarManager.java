package at.fhv.audioracer.simulator.world;

public interface ICarManager {
	public void connect(int zigbeeId);
	
	public void disconnect(int zigbeeId);
	
	public void invalidCommand(int zigbeeId);
}
