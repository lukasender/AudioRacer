package at.fhv.audioracer.simulator.player.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TextInput;
import org.apache.pivot.wtk.TextInputContentListener;

public class PlayerNameForm extends Form implements Bindable {
	
	@BXML
	private TextInput _textInputName;
	@BXML
	private PushButton _pushButton;
	
	public PlayerNameForm() {
		
	}
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_textInputName.getTextInputContentListeners().add(new TextInputContentListener.Adapter() {
			@Override
			public void textChanged(TextInput textInput) {
				String text = textInput.getText();
				_pushButton.setEnabled(text != null && !text.isEmpty());
			}
		});
		
		_pushButton.setEnabled(false);
		_pushButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				PlayerSimulatorWindow.getInstance().setPlayerName(_textInputName.getText());
				PlayerSimulatorWindow.getInstance().setContent(ServerDiscoveryForm.class);
			}
		});
	}
	
}
