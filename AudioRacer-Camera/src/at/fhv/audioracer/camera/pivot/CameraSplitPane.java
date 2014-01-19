package at.fhv.audioracer.camera.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.Spinner;
import org.apache.pivot.wtk.SpinnerSelectionListener;
import org.apache.pivot.wtk.SplitPane;

public class CameraSplitPane extends SplitPane implements Bindable {
	
	@BXML
	private Spinner _cameraIdSpinner;
	@BXML
	PushButton _cameraSelectedButton;
	@BXML
	private PushButton _startPositioningButton;
	@BXML
	private PushButton _rotateButton;
	@BXML
	private PushButton _startCalibrationButton;
	@BXML
	private PushButton _calibrationFinishedButton;
	@BXML
	private PushButton _gameAreaSelectedButton;
	@BXML
	private PushButton _allCarsDetectedButton;
	
	@BXML
	private CameraMapComponent _cameraMapComponent;
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_startPositioningButton.setEnabled(false);
		_rotateButton.setEnabled(false);
		_startCalibrationButton.setEnabled(false);
		_calibrationFinishedButton.setEnabled(false);
		_gameAreaSelectedButton.setEnabled(false);
		_allCarsDetectedButton.setEnabled(false);
		
		_cameraIdSpinner.getSpinnerSelectionListeners().add(new SpinnerSelectionListener.Adapter() {
			@Override
			public void selectedItemChanged(Spinner spinner, Object previousSelectedItem) {
				int id = (int) _cameraIdSpinner.getSelectedItem();
				_cameraMapComponent.selectCamera(id);
			}
		});
		_cameraMapComponent.selectCamera((int) _cameraIdSpinner.getSelectedItem());
		_cameraSelectedButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_cameraIdSpinner.setEnabled(false);
				_cameraSelectedButton.setEnabled(false);
				_startPositioningButton.setEnabled(true);
				
				_cameraMapComponent.setDrawCheesboard(true);
			}
		});
		
		_startPositioningButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				if (!_cameraMapComponent.startPositioning()) {
					Alert.alert("No cheesboard detected!", getWindow());
					return;
				}
				
				_cameraMapComponent.setDrawCheesboard(false);
				
				_startPositioningButton.setEnabled(false);
				_rotateButton.setEnabled(true);
				_startCalibrationButton.setEnabled(true);
			}
		});
		_rotateButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_cameraMapComponent.rotate();
			}
		});
		_startCalibrationButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_rotateButton.setEnabled(false);
				_startCalibrationButton.setEnabled(false);
				_calibrationFinishedButton.setEnabled(true);
				
				_cameraMapComponent.startCalibration();
			}
		});
		
		_calibrationFinishedButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_calibrationFinishedButton.setEnabled(false);
				_gameAreaSelectedButton.setEnabled(true);
				
				_cameraMapComponent.endCalibration();
			}
		});
		
		_gameAreaSelectedButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_gameAreaSelectedButton.setEnabled(false);
				_allCarsDetectedButton.setEnabled(true);
				
				_cameraMapComponent.gameAreaSelected();
			}
		});
		
		_allCarsDetectedButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_allCarsDetectedButton.setEnabled(false);
				// TODO: inform server
			}
		});
	}
}
