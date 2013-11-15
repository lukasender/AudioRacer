package at.fhv.audioracer.communication.player.message;

public class PlayerConnectedMessage extends PlayerMessage {
	
	public int id;
	public String playerName;
	
	public PlayerConnectedMessage() {
		super(MessageId.PLAYER_CONNECTED);
	}
}
