package at.fhv.audioracer.communication.world;

public interface ICarClient {
	
	public int getCarClientId();
	
	public void updateVelocity(float speed, float direction);
}
