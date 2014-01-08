package at.fhv.audioracer.communication.player.message;

public class UpdateCheckPointDirectionMessage extends PlayerMessage {
	
	public float posX;
	public float posY;
	
	public UpdateCheckPointDirectionMessage() {
		super(MessageId.UPDATE_CHECKPOINT_DIRECTION);
	}
}
