package at.fhv.audioracer.communication.player.message;

public class ConnectResponseMessage extends PlayerMessage {
	
	public ConnectResponseMessage() {
		super(PlayerMessage.MessageId.CONNECT_RESPONSE);
	}
	
	public int playerId;
}
