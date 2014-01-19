package at.fhv.audioracer.camera.pivot;

import java.io.IOException;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;

import at.fhv.audioracer.camera.OpenCVCamera;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.communication.world.message.ConfigureMapMessage;

import com.esotericsoftware.kryonet.Client;

public class CameraApplication implements Application {
	
	private static CameraApplication _instance;
	
	private Client _cameraClient;
	
	private OpenCVCamera _camera;
	
	public CameraApplication() {
		_instance = this;
		
		_cameraClient = new Client(30 * 81920, 20 * 2048);
		_cameraClient.start();
		WorldNetwork.register(_cameraClient);
	}
	
	public static CameraApplication getInstance() {
		return _instance;
	}
	
	public void connect(String host) throws IOException {
		_cameraClient.connect(1000, host, WorldNetwork.CAMERA_SERVICE_PORT);
	}
	
	public void configureMap(at.fhv.audioracer.core.model.Map map) {
		ConfigureMapMessage msg = new ConfigureMapMessage();
		msg.sizeX = map.getSizeX();
		msg.sizeY = map.getSizeY();
		_cameraClient.sendTCP(msg);
	}
	
	public void setCamera(OpenCVCamera camera) {
		_camera = camera;
	}
	
	@Override
	public void startup(Display display, Map<String, String> properties) throws Exception {
		BXMLSerializer bxml = new BXMLSerializer();
		Window window = (Window) bxml.readObject(CameraApplication.class, "CameraWindow.bxml");
		window.open(display);
	}
	
	@Override
	public boolean shutdown(boolean optional) throws Exception {
		if (_camera != null) {
			_camera.stop(); // TODO: implement as listener
		}
		return false;
	}
	
	@Override
	public void suspend() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) {
		DesktopApplicationContext.main(CameraApplication.class, args);
	}
	
}
