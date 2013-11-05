package at.fhv.audioracer.server.proxy;

import java.io.IOException;
import java.net.InetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICamera;
import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.server.CarManager;
import at.fhv.audioracer.server.Main;
import at.fhv.audioracer.server.dao.CarProxy;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class CameraCommunicationProxy extends Connection implements ICamera {
	
	private static Logger _logger = LoggerFactory.getLogger(CameraCommunicationProxy.class);
	
	@Override
	public void configureMap(int sizeX, int sizeY) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void updateCar(int carId, float posX, float posY, float direction) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void carDetected(int carId, byte[] image) {
		_logger.debug("carDetected with id: {} called.", carId);
		
		try {
			CarProxy car = new CarProxy(carId);
			
			Client client = new Client();
			client.start();
			
			WorldNetwork.register(client);
			
			ICarClient carClient = ObjectSpace.getRemoteObject(client, WorldNetwork.CAR_SERVICE, ICarClient.class);
			RemoteObject obj = (RemoteObject) carClient;
			obj.setTransmitExceptions(false);
			
			// Register ICarClientManager
			CarCommunicationProxy proxy = new CarCommunicationProxy(carId);
			ObjectSpace objectSpace = new ObjectSpace(client);
			objectSpace.setExecutor(Main.executor);
			objectSpace.register(WorldNetwork.CAR_SERVICE, proxy);
			
			proxy.setCarClient(carClient);
			
			client.connect(1000, InetAddress.getLoopbackAddress(), WorldNetwork.CAR_SERVICE_PORT);
			car.setProxy(proxy);
			
			// Test purpose only
			// call RemoteObject ICarClient
			proxy.updateVelocity(10, 20);
			
			// TODO: add free car to car manager
			
		} catch (IOException e) {
			_logger.error("Exception caught in carDetected!", e);
		}
	}
	
	@Override
	public void detectionFinished() {
		// TODO Auto-generated method stub
	}
}
