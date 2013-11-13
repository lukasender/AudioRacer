package at.fhv.audioracer.communication.player.message;

public class SelectCarResponseMessage extends PlayerMessage {
	
	public boolean selectionSuccess;
	
	public SelectCarResponseMessage() {
		super(MessageId.SELECT_CAR_RESPONSE);
	}
}
