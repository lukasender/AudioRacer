package at.fhv.audioracer.communication.player.message;

public class ConnectRequestMessage extends PlayerMessage {
	
	public ConnectRequestMessage() {
		super(PlayerMessage.MessageId.CONNECT_REQUEST);
	}
	
	public String playerName;
}
