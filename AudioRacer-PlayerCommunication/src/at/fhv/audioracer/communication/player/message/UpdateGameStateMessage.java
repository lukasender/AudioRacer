package at.fhv.audioracer.communication.player.message;

public class UpdateGameStateMessage extends PlayerMessage {
	
	public byte carId;
	public int coinsLeft;
	public int time;
	
	public UpdateGameStateMessage() {
		super(MessageId.UPDATE_GAME_STATE);
	}
}
