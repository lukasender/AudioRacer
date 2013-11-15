package at.fhv.audioracer.communication.player.message;

public class SetPlayerNameResponseMessage extends PlayerMessage {
	
	public SetPlayerNameResponseMessage() {
		super(PlayerMessage.MessageId.SET_PLAYER_NAME_RESPONSE);
	}
	
	public int playerId;
}
