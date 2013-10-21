package at.fhv.audioracer.communication.world;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;

public class WorldNetwork {
	public static final int CAR_SERVICE = 13;
	public static final int CAMERA_SERVICE = 14;
	
	public static final int CAR_SERVICE_PORT = 4712;
	public static final int CAMERA_SERVICE_PORT = 4711;
	
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		// This must be called in order to use ObjectSpaces.
		ObjectSpace.registerClasses(kryo);
		
		// The interfaces that will be used as remote objects must be registered.
		kryo.register(ICarClient.class);
		kryo.register(ICarClientManager.class);
		kryo.register(ICamera.class);
		kryo.register(byte[].class);
	}
}
