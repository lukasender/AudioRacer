package at.fhv.audioracer.communication.player.message;

public class PlayerConnectedMessage extends PlayerMessage {
	
	public int id;
	public String loginName;
	
	public PlayerConnectedMessage() {
		super(MessageId.PLAYER_CONNECTED);
	}
}
