package at.fhv.audioracer.simulator.world.pivot;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.URL;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.Window;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.communication.world.message.CameraMessage;
import at.fhv.audioracer.communication.world.message.CameraMessage.MessageId;
import at.fhv.audioracer.communication.world.message.CarDetectedMessage;
import at.fhv.audioracer.communication.world.message.ConfigureMapMessage;
import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.simulator.world.SimulationController;
import at.fhv.audioracer.ui.pivot.MapComponent;

import com.esotericsoftware.kryonet.Client;

public class WorldSimulatorWindow extends Window implements Application, Bindable {
	
	@BXML
	private MapComponent _map;
	
	private Window _window;
	private static final Logger _logger = LoggerFactory.getLogger(WorldSimulatorWindow.class);
	public static final Executor executor = Executors.newSingleThreadExecutor();
	
	private static Client _cameraClient;
	
	// private static ArrayList<CarCommunicationProxy> _carList = new ArrayList<CarCommunicationProxy>();
	
	@Override
	public void initialize(org.apache.pivot.collections.Map<String, Object> namespace, URL location, Resources resources) {
		System.out.println("initialize()");
		try {
			SimulationController.getInstance().setUp(_map, new Map(20, 30));
		} catch (OperationNotSupportedException e1) {
			String msg = "Couldn't initialize the map.";
			Alert.alert(MessageType.ERROR, msg, this);
			// We should probably disable some of the GUI's functionality
			_logger.error(msg, e1);
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					while (true) {
						SimulationController.getInstance().update();
					}
				} catch (InterruptedException e) {
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
	}
	
	@Override
	public void resume() throws Exception {
	}
	
	@Override
	public boolean shutdown(boolean optional) throws Exception {
		return false;
	}
	
	public void setView(Component view) {
		_window.setContent(view);
	}
	
	public static void main(String[] args) {
		// new RepeatingReleasedEventsFixer().install();
		// DesktopApplicationContext.main(WorldSimulatorWindow.class, args);
		
		try {
			
			// Test purpose only
			startCameraClient();
			
			ConfigureMapMessage configureMap = new ConfigureMapMessage();
			configureMap.sizeX = 100;
			configureMap.sizeY = 100;
			_cameraClient.sendTCP(configureMap);
			
			CarDetectedMessage carDetected = new CarDetectedMessage();
			
			// read the image and store in byte array
			ClassLoader loader = Thread.currentThread().getContextClassLoader();
			URL carImgURL = loader.getResource("at/fhv/audioracer/ui/pivot/car-blue.png");
			BufferedImage img = ImageIO.read(carImgURL);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			ImageIO.write(img, "png", out);
			out.flush();
			
			carDetected.carId = 1;
			carDetected.image = out.toByteArray();
			out.close();
			
			_cameraClient.sendTCP(carDetected);
			
			CameraMessage detecionFinished = new CameraMessage(MessageId.DETECTION_FINISHED);
			_cameraClient.sendTCP(detecionFinished);
			
			// Kryo Clients are running as daemons, prevent main application from exit
			while (true) {
			}
			
		} catch (Exception e) {
			_logger.error("Exception caught during startup!", e);
			
			if (_cameraClient != null) {
				_cameraClient.close();
			}
			// TODO: Are this all connections we need to close?
		}
	}
	
	private static void startCameraClient() throws IOException {
		// _cameraClient = new Client();
		_cameraClient = new Client(81920, 2048);
		_cameraClient.start();
		
		WorldNetwork.register(_cameraClient);
		
		_cameraClient.connect(1000, InetAddress.getLoopbackAddress(), WorldNetwork.CAMERA_SERVICE_PORT);
	}
	
}
