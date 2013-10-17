package at.fhv.audioracer.communication.world;

public interface ICamera {
	public void configureMap(int sizeX, int sizeY);
	
	public void updateCar(int carId, float posX, float posY, float direction);
	
	// TODO: image als byte[]?
	// TODO: wofür Parameter size?
	public void carDetected(int carId, byte[] image);
	
	public void detectionFinished();
}
