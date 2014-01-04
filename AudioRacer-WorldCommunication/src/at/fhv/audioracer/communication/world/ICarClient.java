package at.fhv.audioracer.communication.world;

public interface ICarClient {
	
	public byte getCarClientId();
	
	public void updateVelocity(float speed, float direction);
	
	public void trim();
}
