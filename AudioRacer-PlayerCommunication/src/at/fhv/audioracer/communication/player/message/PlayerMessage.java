package at.fhv.audioracer.communication.player.message;

public class PlayerMessage {
	
	// @formatter:off
	public enum MessageId {
		DISCONNECT, 
		GAME_START,
		GET_CAR_IMG_REQUEST,
		GET_CAR_IMG_RESPONSE,
		PLAYER_CONNECTED,
		PLAYER_DISCONNECTED,
		SELECT_CAR_REQUEST,
		SELECT_CAR_RESPONSE,
		SET_PLAYER_NAME_REQUEST,
		SET_PLAYER_NAME_RESPONSE,
		SET_READY,
		UPDATE_CHECKPOINT_DIRECTION,
		UPDATE_FREE_CARS,
		UPDATE_VELOCITY,
		;
	}
	// @formatter:on
	
	public PlayerMessage() {
	}
	
	public PlayerMessage(MessageId id) {
		messageId = id;
	}
	
	public MessageId messageId;
}
