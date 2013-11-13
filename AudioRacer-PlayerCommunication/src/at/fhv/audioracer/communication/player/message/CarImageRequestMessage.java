package at.fhv.audioracer.communication.player.message;

public class CarImageRequestMessage extends PlayerMessage {
	
	public int carId;
	
	public CarImageRequestMessage() {
		super(MessageId.GET_CAR_IMG_REQUEST);
	}
	
}
