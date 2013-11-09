package at.fhv.audioracer.simulator.world.pivot;

import java.io.IOException;
import java.net.URL;

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

import at.fhv.audioracer.simulator.world.SimulationController;

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
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_mapConfiguredButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				Integer x = convertTextInput(_sizeXTextInput, "'x' must be an integer.");
				Integer y = convertTextInput(_sizeYTextInput, "'y' must be an integer.");
				SimulationController.getInstance().updateMap(x, y);
			}
		});
		
		_addCarButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				try {
					SimulationController.getInstance().addCar();
				} catch (IOException e) {
					Alert.alert(MessageType.ERROR, "Couldn't create CarClient properly.", ConfigurationBorder.this.getWindow());
				}
			}
		});
		
		_removeCarButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				SimulationController.getInstance().removeCar();
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
}
