package at.fhv.audioracer.communication.player.message;

public class PlayerMessage {
	
	public enum MessageId {
		CONNECT_REQUEST, CONNECT_RESPONSE, DISCONNECT, UPDATE_VELOCITY, SELECT_CAR_REQUEST, SELECT_CAR_RESPONSE, GET_CAR_IMG_REQUEST, GET_CAR_IMG_RESPONSE, SET_READY;
	}
	
	// public static final int CONNECT_REQUEST = 1;
	// public static final int CONNECT_RESPONSE = 2;
	// public static final int DISCONNECT = 3;
	// public static final int UPDATE_VELOCITY = 4;
	// public static final int SELECT_CAR_REQUEST = 5;
	// public static final int SELECT_CAR_RESPONSE = 6;
	// public static final int GET_CAR_IMG_REQUEST = 7;
	// public static final int GET_CAR_IMG_RESPONSE = 8;
	// public static final int SET_READY = 9;
	
	public PlayerMessage(MessageId id) {
		messageId = id;
	}
	
	public MessageId messageId;
}
