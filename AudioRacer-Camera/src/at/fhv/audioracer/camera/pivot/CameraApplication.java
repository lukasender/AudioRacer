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
import at.fhv.audioracer.communication.world.message.CameraMessage;
import at.fhv.audioracer.communication.world.message.CameraMessage.MessageId;
import at.fhv.audioracer.communication.world.message.CarDetectedMessage;
import at.fhv.audioracer.communication.world.message.ConfigureMapMessage;
import at.fhv.audioracer.communication.world.message.UpdateCarMessage;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.ICarListener;
import at.fhv.audioracer.core.model.IMapListener;

import com.esotericsoftware.kryonet.Client;

public class CameraApplication implements Application, IMapListener, ICarListener {
	
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
	
	public void allCarsDected() {
		_cameraClient.sendTCP(new CameraMessage(MessageId.DETECTION_FINISHED));
	}
	
	public void setCamera(OpenCVCamera camera) {
		_camera = camera;
	}
	
	@Override
	public void onMapSizeChanged() {
		// no-op.
	}
	
	@Override
	public void onCarAdded(Car<?> addedCar) {
		addedCar.getCarListenerList().add(this);
		
		CarDetectedMessage msg = new CarDetectedMessage();
		msg.carId = addedCar.getCarId();
		msg.posX = addedCar.getPosition().getPosX();
		msg.posY = addedCar.getPosition().getPosY();
		msg.direction = addedCar.getDirection().getDirection();
		msg.image = null; // TODO: implement
		_cameraClient.sendTCP(msg);
	}
	
	@Override
	public void onCarRemoved(Car<?> removedCar) {
		// TODO: does a car ever get removed?
	}
	
	@Override
	public void onCheckpointChange() {
		// no-op.
	}
	
	@Override
	public void onCarPositionChanged(Car<?> car) {
		UpdateCarMessage msg = new UpdateCarMessage();
		msg.carId = car.getCarId();
		msg.direction = car.getDirection().getDirection();
		msg.posX = car.getPosition().getPosX();
		msg.posY = car.getPosition().getPosY();
		_cameraClient.sendTCP(msg); // TODO: why not UDP?
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
