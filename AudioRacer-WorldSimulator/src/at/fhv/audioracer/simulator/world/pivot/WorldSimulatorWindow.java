package at.fhv.audioracer.simulator.world.pivot;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.naming.OperationNotSupportedException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.util.Resources;
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
import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.simulator.proxy.CarCommunicationProxy;
import at.fhv.audioracer.simulator.world.Initializer;
import at.fhv.audioracer.ui.pivot.MapComponent;
import at.fhv.audioracer.ui.util.awt.RepeatingReleasedEventsFixer;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class WorldSimulatorWindow extends Window implements Application, Bindable {

	@BXML
	private MapComponent _map;

	private Window _window;
	private static final Logger _logger = LoggerFactory.getLogger(WorldSimulatorWindow.class);
	public static final Executor executor = Executors.newSingleThreadExecutor();

	private static Server _carServer;
	private static Client _cameraClient;
	private static ICamera _camera;

	// private static ArrayList<CarCommunicationProxy> _carList = new ArrayList<CarCommunicationProxy>();

	@Override
	public void initialize(org.apache.pivot.collections.Map<String, Object> namespace, URL location, Resources resources) {
		System.out.println("initialize()");
		try {
			Initializer.getInstance().setUp(_map, new Map(20, 30));
		} catch (OperationNotSupportedException e1) {
			e1.printStackTrace();
		}

		new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					while (true) {
						// car.updatePosition(new Position(15, 15), new Direction(90));
						// Thread.sleep(1000);
						// car.updatePosition(new Position(10, 20), new Direction(180));
						// Thread.sleep(1000);
						// car.updatePosition(new Position(5, 15), new Direction(270));
						// Thread.sleep(1000);
						// car.updatePosition(new Position(10, 10), new Direction(0));
						// Thread.sleep(1000);
						Initializer.getInstance().update();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();

	}

	@Override
	public void startup(Display display, org.apache.pivot.collections.Map<String, String> properties) throws Exception {
		System.out.println("startup()");
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

			// for (CarCommunicationProxy proxy : _carList) {
			// proxy.connect();
			// }

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

				// _carList.add(proxy);

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
