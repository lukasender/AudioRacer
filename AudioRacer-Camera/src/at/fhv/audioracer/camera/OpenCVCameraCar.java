package at.fhv.audioracer.camera;

import java.io.IOException;

import javax.imageio.ImageIO;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.ui.pivot.MapComponent;

public class OpenCVCameraCar {
	
	private Car<?> _car;
	
	public OpenCVCameraCar() {
	}
	
	public void setHueRange(int colorLower, int colorUpper, int saturationLower,
			int saturationUpper, int valueLower, int valueUpper) {
		
	}
	
	/**
	 * call to create a detected Object as Car in Model
	 * 
	 * @param id
	 * @param position
	 * @param direction
	 * @return true if car was created
	 */
	public boolean carDetected(byte id, Position position, Direction direction) {
		try {
			_car = new Car<>(id, position, direction, ImageIO.read(MapComponent.class
					.getResource("car-red.png")));
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	public void updatePosition(Position position, Direction direction) {
		_car.updatePosition(position, direction);
	}
}
