package at.fhv.audioracer.communication.world.message;

/**
 * Camera Base Message.
 * 
 * @author edi
 */
public class CameraMessage {
	
	public enum MessageId {
		CONFIGURE_MAP, UPDATE_CAR, CAR_DETECTED, DETECTION_FINISHED
	}
	
	public MessageId messageId;
	
	public CameraMessage() {
	}
	
	public CameraMessage(MessageId id) {
		messageId = id;
	}
}
