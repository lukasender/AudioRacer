package at.fhv.audioracer.communication.player.message;

public class ReconnectRequestResponse extends PlayerMessage {
	
	public boolean reconnectSuccess;
	
	public ReconnectRequestResponse() {
		super(MessageId.RECONNECT_RESPONSE);
	}
}
