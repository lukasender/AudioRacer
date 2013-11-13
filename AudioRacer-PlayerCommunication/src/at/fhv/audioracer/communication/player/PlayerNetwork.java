package at.fhv.audioracer.communication.player;

import at.fhv.audioracer.communication.player.message.ConnectRequestMessage;
import at.fhv.audioracer.communication.player.message.ConnectResponseMessage;
import at.fhv.audioracer.communication.player.message.FreeCarsMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage;

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
		kryo.register(ConnectRequestMessage.class);
		kryo.register(ConnectResponseMessage.class);
		kryo.register(FreeCarsMessage.class);
		kryo.register(int[].class);
	}
	
}
