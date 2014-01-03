package at.fhv.audioracer.communication.player.message;

public class SelectCarRequestMessage extends PlayerMessage {
	
	public byte carId;
	
	public SelectCarRequestMessage() {
		super(MessageId.SELECT_CAR_REQUEST);
	}
	
}
