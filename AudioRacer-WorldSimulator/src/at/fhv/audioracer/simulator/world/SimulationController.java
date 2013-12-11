package at.fhv.audioracer.simulator.world;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.naming.OperationNotSupportedException;

import org.apache.log4j.Logger;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.communication.world.ICarClientManager;
import at.fhv.audioracer.communication.world.message.CameraMessage;
import at.fhv.audioracer.communication.world.message.CameraMessage.MessageId;
import at.fhv.audioracer.communication.world.message.CarDetectedMessage;
import at.fhv.audioracer.communication.world.message.ConfigureMapMessage;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.ICarListener;
import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.server.CarClientManager;
import at.fhv.audioracer.simulator.world.impl.CarClient;
import at.fhv.audioracer.simulator.world.impl.CarClientListener;
import at.fhv.audioracer.simulator.world.impl.exception.NoCarsAddedException;
import at.fhv.audioracer.simulator.world.pivot.WorldSimulatorWindow;
import at.fhv.audioracer.ui.pivot.MapComponent;

import com.esotericsoftware.kryonet.Client;

// TODO: This class name may change.
/**
 * Takes care of the initialization of the logic of the simulator.
 * 
 * @author lumannnn
 * 
 */
public class SimulationController {
	
	private static final Logger logger = Logger.getLogger(SimulationController.class);
	
	private static SimulationController _instance;
	
	private ICarClientManager _carClientManager;
	private static Client _camera;
	
	private MapComponent _map;
	
	private int _carId;
	
	private List<ICarClient> _carClients;
	
	private Position _lastCarPos;
	private static float TRANSLATE_BY = 4;
	
	private SimulationController() {
		_carId = 0;
		_lastCarPos = new Position(0, 0);
		_carClients = new LinkedList<ICarClient>();
		
		// establish the connections
		_carClientManager = CarClientManager.getInstance();
		_camera = WorldSimulatorWindow.getCameraClient();
	}
	
	public static SimulationController getInstance() {
		if (_instance == null) {
			_instance = new SimulationController();
		}
		
		return _instance;
	}
	
	public void setUp(MapComponent mapComponent, Map map) throws OperationNotSupportedException {
		setMap(mapComponent);
		getMapComponent().setMap(map);
		_camera.sendTCP(createConfigureMapMessage(map));
	}
	
	public void updateMap(int width, int height) {
		Map map = getMap();
		map.setSizeX(width);
		map.setSizeY(height);
		_camera.sendTCP(createConfigureMapMessage(map));
		getMapComponent().repaint();
	}
	
	public void addCar() throws IOException {
		// add car to map
		BufferedImage image = ImageIO.read(MapComponent.class.getResource("car-red.png"));
		Car car = new Car(_carId, _lastCarPos, new Direction(90), image);
		translateLastCarPosX(TRANSLATE_BY);
		getMap().addCar(car);
		
		ICarListener carListener = new CarClientListener();
		car.getCarListenerList().add(carListener);
		
		ICarClient carClient = new CarClient();
		((CarClient) carClient).setCar(car); // this only needs to be done in the simulation
		
		_carClientManager.connect(carClient);
		_carClients.add(carClient);
		
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		ImageIO.write(image, "png", out);
		out.flush();
		_camera.sendTCP(createCarDetectedMessage(_carId, out.toByteArray()));
		
		_carId++;
		
		logger.info("added car with id: " + (_carId - 1));
	}
	
	public void removeCar() {
		if (_carId > 0) {
			translateLastCarPosX(-TRANSLATE_BY);
			getMap().removeCar(--_carId);
			logger.info("removed car with id: " + (_carId));
		}
		logger.info("There are no cars to be removed.");
	}
	
	public void allCarsDetected() throws NoCarsAddedException {
		if (_carId > 0) {
			_camera.sendTCP(createAllCarsDetectionFinishedMessage());
		} else {
			throw new NoCarsAddedException();
		}
	}
	
	private void setMap(MapComponent map) {
		if (map == null) {
			throw new IllegalArgumentException("map must not be null");
		}
		_map = map;
	}
	
	private MapComponent getMapComponent() {
		return _map;
	}
	
	private Map getMap() {
		return getMapComponent().getMap();
	}
	
	public void update() {
		// int x = getMap().getMap().getSizeX();
		// int y = getMap().getMap().getSizeY();
		// System.out.println("MapSize: " + x + ", " + y);
		
	}
	
	public Client getCamera() {
		return _camera;
	}
	
	// helper methods
	
	private ConfigureMapMessage createConfigureMapMessage(Map map) {
		ConfigureMapMessage configMap = new ConfigureMapMessage();
		configMap.sizeX = map.getSizeX();
		configMap.sizeY = map.getSizeY();
		return configMap;
	}
	
	private CarDetectedMessage createCarDetectedMessage(int id, byte[] image) {
		CarDetectedMessage carDetectedMessage = new CarDetectedMessage();
		carDetectedMessage.carId = _carId;
		carDetectedMessage.image = image;
		return carDetectedMessage;
	}
	
	private CameraMessage createAllCarsDetectionFinishedMessage() {
		CameraMessage detectionFinished = new CameraMessage(MessageId.DETECTION_FINISHED);
		return detectionFinished;
	}
	
	private void translateLastCarPosX(float x) {
		_lastCarPos = new Position(_lastCarPos.getPosX() + x, _lastCarPos.getPosY());
	}
	
}
