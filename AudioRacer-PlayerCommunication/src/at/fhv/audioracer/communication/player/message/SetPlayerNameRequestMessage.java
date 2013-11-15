package at.fhv.audioracer.communication.player.message;

public class SetPlayerNameRequestMessage extends PlayerMessage {
	
	public SetPlayerNameRequestMessage() {
		super(PlayerMessage.MessageId.SET_PLAYER_NAME_REQUEST);
	}
	
	public String playerName;
}
