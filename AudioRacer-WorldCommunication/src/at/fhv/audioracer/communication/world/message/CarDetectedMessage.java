package at.fhv.audioracer.communication.world.message;

public class CarDetectedMessage extends CameraMessage {
	
	public int carId;
	public byte[] image;
	
	public CarDetectedMessage() {
		super(MessageId.CAR_DETECTED);
	}
}
