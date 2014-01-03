package at.fhv.audioracer.communication.world.message;

public class CarDetectedMessage extends CameraMessage {
	
	public byte carId;
	public float posX;
	public float posY;
	public float direction;
	public byte[] image;
	
	public CarDetectedMessage() {
		super(MessageId.CAR_DETECTED);
	}
}
