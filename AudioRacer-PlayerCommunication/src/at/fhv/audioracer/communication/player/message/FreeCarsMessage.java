package at.fhv.audioracer.communication.player.message;

public class FreeCarsMessage extends PlayerMessage {
	
	public byte[] freeCars;
	
	public FreeCarsMessage() {
		super(PlayerMessage.MessageId.UPDATE_FREE_CARS);
	}
}
