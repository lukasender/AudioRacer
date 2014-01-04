package at.fhv.audioracer.communication.player.message;

public class UpdateCheckPointDirectionMessage extends PlayerMessage {
	
	public float angel;
	public float distance;
	
	public UpdateCheckPointDirectionMessage() {
		super(MessageId.UPDATE_CHECKPOINT_DIRECTION);
	}
}
