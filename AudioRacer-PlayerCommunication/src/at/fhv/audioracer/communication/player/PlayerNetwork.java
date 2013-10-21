package at.fhv.audioracer.communication.player;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class PlayerNetwork {
	
	public static final int PLAYER_MANAGER = 0;
	public static final int PLAYER_CLIENT = 1;
	
	public static final int PLAYER_SERVICE_PORT = 4714;
	
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		// This must be called in order to use ObjectSpaces.
		ObjectSpace.registerClasses(kryo);
		
		// The interfaces that will be used as remote objects must be registered.
		kryo.register(IPlayerClientManager.class);
		kryo.register(IPlayerClient.class);
		// The classes of all method parameters and return values
		// for remote objects must also be registered.
		kryo.register(int[].class);
		kryo.register(byte[].class);
	}
	
}
