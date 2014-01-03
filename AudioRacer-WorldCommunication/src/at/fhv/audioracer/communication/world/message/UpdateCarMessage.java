package at.fhv.audioracer.communication.world.message;

public class UpdateCarMessage extends CameraMessage {
	
	public byte carId;
	public float posX;
	public float posY;
	public float direction;
	
	public UpdateCarMessage() {
		super(MessageId.UPDATE_CAR);
	}
}
