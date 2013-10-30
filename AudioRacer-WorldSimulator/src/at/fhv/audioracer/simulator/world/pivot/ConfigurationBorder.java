package at.fhv.audioracer.simulator.world.pivot;

import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.MessageType;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;
import at.fhv.audioracer.simulator.world.Initializer;
import at.fhv.audioracer.ui.pivot.MapComponent;

public class ConfigurationBorder extends Border implements Bindable {
	@BXML
	private TextInput _sizeXTextInput;
	@BXML
	private TextInput _sizeYTextInput;
	@BXML
	private PushButton _mapConfiguredButton;
	
	@BXML
	private PushButton _addCarButton;
	@BXML
	private PushButton _removeCarButton;
	@BXML
	private PushButton _allCarsDetectedButton;
	
	// TODO: this isn't the right place to store it.
	private int carId = 0;
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_mapConfiguredButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				Integer x = convertTextInput(_sizeXTextInput, "'x' must be an integer.");
				Integer y = convertTextInput(_sizeYTextInput, "'y' must be an integer.");
				at.fhv.audioracer.core.model.Map map = getMap();
				map.setSizeX((x != null) ? x : map.getSizeX());
				map.setSizeY((y != null) ? y : map.getSizeY());
				Initializer.getInstance().getMap().repaint();
			}
		});
		
		_addCarButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				try {
					Car car = new Car(carId++, new Position(0, 0), new Direction(0), ImageIO.read(MapComponent.class.getResource("car-red.png")));
					getMap().addCar(car);
					System.out.println("added car with id: " + (carId - 1));
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		_removeCarButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				if (carId > 0) {
					getMap().removeCar(--carId);
					System.out.println("removed car with id: " + (carId));
				}
			}
		});
		
	}
	
	private Integer convertTextInput(TextInput ti, String message) {
		try {
			return Integer.parseInt(ti.getText());
		} catch (NumberFormatException e) {
			Alert.alert(MessageType.WARNING, message, ConfigurationBorder.this.getWindow());
		}
		return null;
	}
	
	private at.fhv.audioracer.core.model.Map getMap() {
		return Initializer.getInstance().getMap().getMap();
	}
}
