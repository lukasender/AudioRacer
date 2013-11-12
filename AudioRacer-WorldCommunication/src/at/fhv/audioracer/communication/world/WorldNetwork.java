package at.fhv.audioracer.communication.world;

import at.fhv.audioracer.communication.world.message.CameraMessage;
import at.fhv.audioracer.communication.world.message.CarDetectedMessage;
import at.fhv.audioracer.communication.world.message.ConfigureMapMessage;
import at.fhv.audioracer.communication.world.message.UpdateCarMessage;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;

public class WorldNetwork {
	public static final int CAR_SERVICE = 13;
	public static final int CAMERA_SERVICE = 14;
	
	public static final int CAR_SERVICE_PORT = 4712;
	public static final int CAMERA_SERVICE_PORT = 4711;
	
	public static void register(EndPoint endpoint) {
		Kryo kryo = endpoint.getKryo();
		
		kryo.register(byte[].class);
		kryo.register(CameraMessage.class);
		kryo.register(CameraMessage.MessageId.class);
		kryo.register(CarDetectedMessage.class);
		kryo.register(ConfigureMapMessage.class);
		kryo.register(UpdateCarMessage.class);
	}
}
