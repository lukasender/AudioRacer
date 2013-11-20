package at.fhv.audioracer.communication.player.message;

public class PlayerDisconnectedMessage extends PlayerMessage {
	
	public int playerId;
	
	public PlayerDisconnectedMessage() {
		super(MessageId.PLAYER_DISCONNECTED);
	}
}
