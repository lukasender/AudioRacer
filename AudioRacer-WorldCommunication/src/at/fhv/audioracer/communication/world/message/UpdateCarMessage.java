package at.fhv.audioracer.communication.world.message;

public class UpdateCarMessage extends CameraMessage {
	
	public byte carId;
	public float posX;
	public float posY;
	public float direction;
	public int seqNr;
	private static int _seqNr;
	
	public UpdateCarMessage() {
		super(MessageId.UPDATE_CAR);
		if (++_seqNr < 0) {
			_seqNr = 0;
		}
		seqNr = _seqNr;
		
	}
}
