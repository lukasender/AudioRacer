package at.fhv.audioracer.simulator.world.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Border;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;

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
	}
}
