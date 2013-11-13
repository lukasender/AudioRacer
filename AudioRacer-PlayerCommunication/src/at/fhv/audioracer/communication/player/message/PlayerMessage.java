package at.fhv.audioracer.communication.player.message;

public class PlayerMessage {
	
	public enum MessageId {
		CONNECT_REQUEST, CONNECT_RESPONSE, DISCONNECT, UPDATE_VELOCITY, SELECT_CAR_REQUEST, SELECT_CAR_RESPONSE, GET_CAR_IMG_REQUEST, GET_CAR_IMG_RESPONSE, SET_READY, UPDATE_FREE_CARS, PLAYER_CONNECTED;
	}
	
	public PlayerMessage(MessageId id) {
		messageId = id;
	}
	
	public MessageId messageId;
}
