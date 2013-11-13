package at.fhv.audioracer.communication.player.message;

public class CarImageResponseMessage extends PlayerMessage {
	
	public byte[] image;
	
	public CarImageResponseMessage() {
		super(MessageId.GET_CAR_IMG_RESPONSE);
	}
	
}
