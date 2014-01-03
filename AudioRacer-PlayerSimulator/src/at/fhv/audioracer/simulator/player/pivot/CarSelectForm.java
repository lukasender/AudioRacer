package at.fhv.audioracer.simulator.player.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.LinkedList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.ListViewSelectionListener;
import org.apache.pivot.wtk.PushButton;

import at.fhv.audioracer.client.player.IPlayerClientListener;
import at.fhv.audioracer.client.player.PlayerClient;

public class CarSelectForm extends Form implements Bindable {
	
	@BXML
	private ListView _listView;
	@BXML
	private PushButton _pushButtonSelectCar;
	
	private List<Byte> _freeCars;
	
	private PlayerClient _playerClient;
	private IPlayerClientListener _playerClientListener;
	
	public CarSelectForm() {
		_playerClient = PlayerSimulatorWindow.getInstance().getPlayerClient();
		_freeCars = new LinkedList<Byte>();
	}
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_playerClientListener = new IPlayerClientListener.Adapter() {
			@Override
			public void onUpdateFreeCars() {
				ApplicationContext.queueCallback(new Runnable() {
					public void run() {
						_freeCars.clear();
						for (byte carId : _playerClient.getFreeCarIds()) {
							_freeCars.add(carId);
						}
					}
				});
			}
		};
		_playerClient.getListenerList().add(_playerClientListener);
		for (byte carId : _playerClient.getFreeCarIds()) {
			_freeCars.add(carId);
		}
		_listView.setListData(_freeCars);
		_listView.getListViewSelectionListeners().add(new ListViewSelectionListener.Adapter() {
			@Override
			public void selectedItemChanged(ListView listView, Object previousSelectedItem) {
				_pushButtonSelectCar.setEnabled(listView.getSelectedIndex() >= 0);
			}
		});
		
		_pushButtonSelectCar.setEnabled(false);
		_pushButtonSelectCar.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				if (_playerClient.getPlayerServer().selectCar((byte) _listView.getSelectedItem())) {
					PlayerSimulatorWindow.getInstance().setContent(CarControlSplitPane.class);
					_playerClient.getListenerList().remove(_playerClientListener);
				}
			}
		});
	}
}
