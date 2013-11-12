package at.fhv.audioracer.communication.world.message;

public class ConfigureMapMessage extends CameraMessage {
	
	public int sizeX;
	public int sizeY;
	
	public ConfigureMapMessage() {
		super(CameraMessage.MessageId.CONFIGURE_MAP);
	}
}
