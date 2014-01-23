package at.fhv.audioracer.communication.player.message;

public class ReconnectRequestMessage extends PlayerMessage {
	
	public int playerId;
	
	public ReconnectRequestMessage() {
		super(MessageId.RECONNECT_REQUEST);
	}
}
