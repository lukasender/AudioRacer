package at.fhv.audioracer.simulator.player.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.SplitPane;

import at.fhv.audioracer.client.player.IPlayerClientListener;

public class CarControlSplitPane extends SplitPane implements Bindable {
	
	@BXML
	private PushButton _pushButtonReady;
	@BXML
	private PushButton _pushButtonSelectCar;
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_pushButtonReady.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				_pushButtonReady.setEnabled(false);
				_pushButtonSelectCar.setEnabled(false);
				PlayerSimulatorWindow.getInstance().getPlayerClient().getPlayerServer()
						.setPlayerReady();
			}
		});
		
		_pushButtonSelectCar.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				PlayerSimulatorWindow.getInstance().setContent(CarSelectForm.class);
			}
		});
		
		PlayerSimulatorWindow.getInstance().getPlayerClient().getListenerList()
				.add(new IPlayerClientListener.Adapter() {
					@Override
					public void onGameEnd() {
						ApplicationContext.queueCallback(new Runnable() {
							
							@Override
							public void run() {
								_pushButtonReady.setEnabled(true);
								_pushButtonSelectCar.setEnabled(false);
							}
						});
					}
				});
	}
	
}
