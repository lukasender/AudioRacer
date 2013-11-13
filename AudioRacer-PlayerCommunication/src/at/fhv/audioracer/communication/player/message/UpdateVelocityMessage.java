package at.fhv.audioracer.communication.player.message;

public class UpdateVelocityMessage extends PlayerMessage {
	
	public float speed;
	public float direction;
	
	public UpdateVelocityMessage() {
		super(MessageId.UPDATE_VELOCITY);
	}
	
}
