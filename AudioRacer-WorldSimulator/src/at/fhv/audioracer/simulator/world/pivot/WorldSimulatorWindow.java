package at.fhv.audioracer.simulator.world.pivot;

import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.ICamera;
import at.fhv.audioracer.communication.world.ICarClientManager;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.simulator.proxy.CarCommunicationProxy;
import at.fhv.audioracer.ui.util.awt.RepeatingReleasedEventsFixer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class WorldSimulatorWindow implements Application {
	
	private Window _window;
	private static final Logger _logger = LoggerFactory.getLogger(WorldSimulatorWindow.class);
	public static final Executor executor = Executors.newSingleThreadExecutor();
	
	private static Server _carServer;
	private static Client _cameraClient;
	private static ICamera _camera;
	
	private static ArrayList<CarCommunicationProxy> _carList = new ArrayList<CarCommunicationProxy>();
	
	@Override
	public void startup(Display display, Map<String, String> properties) throws Exception {
		BXMLSerializer bxml = new BXMLSerializer();
		_window = (Window) bxml.readObject(WorldSimulatorWindow.class, "window.bxml");
		_window.open(display);
		
	}
	
	@Override
	public void suspend() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean shutdown(boolean optional) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setView(Component view) {
		_window.setContent(view);
	}
	
	public static void main(String[] args) {
		new RepeatingReleasedEventsFixer().install();
		DesktopApplicationContext.main(WorldSimulatorWindow.class, args);
		
		try {
			
			// Test purpose only
			startCarService();
			startCameraClient();
			
			// Test "run" AudioRacer-Server has to be started
			// for next lines of code
			_camera.carDetected(1, null);
			_camera.carDetected(2, null);
			
			for (CarCommunicationProxy proxy : _carList) {
				proxy.connect();
			}
			
		} catch (Exception e) {
			_logger.error("Exception caught during startup!", e);
			
			if (_carServer != null) {
				_carServer.close();
			}
			if (_cameraClient != null) {
				_cameraClient.close();
			}
			// TODO: Are this all connections we need to close?
		}
	}
	
	private static void startCarService() throws IOException {
		
		_carServer = new Server() {
			
			/**
			 * New connection is established each time ICamera.carDetected is called on Server Side.
			 */
			@Override
			protected Connection newConnection() {
				
				CarCommunicationProxy proxy = new CarCommunicationProxy();
				ObjectSpace objectSapce = new ObjectSpace(proxy);
				objectSapce.setExecutor(executor);
				objectSapce.register(WorldNetwork.CAR_SERVICE, proxy);
				
				ICarClientManager carClientManager = ObjectSpace.getRemoteObject(proxy, WorldNetwork.CAR_SERVICE, ICarClientManager.class);
				RemoteObject obj = (RemoteObject) carClientManager;
				obj.setTransmitExceptions(false);
				
				proxy.setCarClientManager(carClientManager);
				
				_carList.add(proxy);
				
				return proxy;
			}
		};
		
		WorldNetwork.register(_carServer);
		_carServer.bind(WorldNetwork.CAR_SERVICE_PORT);
		_carServer.start();
	}
	
	private static void startCameraClient() throws IOException {
		_cameraClient = new Client();
		_cameraClient.start();
		
		WorldNetwork.register(_cameraClient);
		
		_camera = ObjectSpace.getRemoteObject(_cameraClient, WorldNetwork.CAMERA_SERVICE, ICamera.class);
		RemoteObject obj = (RemoteObject) _camera;
		obj.setTransmitExceptions(false);
		
		_cameraClient.connect(1000, InetAddress.getLoopbackAddress(), WorldNetwork.CAMERA_SERVICE_PORT);
	}
	
}
