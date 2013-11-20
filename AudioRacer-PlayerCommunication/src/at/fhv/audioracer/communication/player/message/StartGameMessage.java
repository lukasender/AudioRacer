package at.fhv.audioracer.communication.player.message;

public class StartGameMessage extends PlayerMessage {
	
	public int gameWillStartInMilliseconds;
	
	public StartGameMessage() {
		super(MessageId.GAME_START);
	}
}
