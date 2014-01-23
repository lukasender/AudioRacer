package at.fhv.audioracer.communication.player;

import at.fhv.audioracer.communication.player.message.FreeCarsMessage;
import at.fhv.audioracer.communication.player.message.PlayerConnectedMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage;
import at.fhv.audioracer.communication.player.message.ReconnectRequestMessage;
import at.fhv.audioracer.communication.player.message.ReconnectRequestResponse;
import at.fhv.audioracer.communication.player.message.SelectCarRequestMessage;
import at.fhv.audioracer.communication.player.message.SelectCarResponseMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameRequestMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameResponseMessage;
import at.fhv.audioracer.communication.player.message.StartGameMessage;
import at.fhv.audioracer.communication.player.message.UpdateCheckPointDirectionMessage;
import at.fhv.audioracer.communication.player.message.UpdateGameStateMessage;
import at.fhv.audioracer.communication.player.message.UpdateVelocityMessage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class PlayerNetwork {
	
	public static final int PLAYER_MANAGER = 0;
	public static final int PLAYER_CLIENT = 1;
	
	public static final int PLAYER_SERVICE_PORT = 4714;
	
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		// This must be called in order to use ObjectSpaces.
		// ObjectSpace.registerClasses(kryo);
		
		// The interfaces that will be used as remote objects must be registered.
		kryo.register(PlayerMessage.MessageId.class);
		kryo.register(SetPlayerNameRequestMessage.class);
		kryo.register(SetPlayerNameResponseMessage.class);
		kryo.register(FreeCarsMessage.class);
		kryo.register(SelectCarRequestMessage.class);
		kryo.register(SelectCarResponseMessage.class);
		kryo.register(UpdateVelocityMessage.class);
		kryo.register(PlayerConnectedMessage.class);
		kryo.register(StartGameMessage.class);
		kryo.register(PlayerMessage.class);
		kryo.register(UpdateGameStateMessage.class);
		kryo.register(UpdateCheckPointDirectionMessage.class);
		kryo.register(ReconnectRequestMessage.class);
		kryo.register(ReconnectRequestResponse.class);
		
		kryo.register(byte[].class);
	}
	
}
