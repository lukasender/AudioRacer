package at.fhv.audioracer.simulator.player.pivot;

import java.io.IOException;
import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.LinkedList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Form;
import org.apache.pivot.wtk.ListView;
import org.apache.pivot.wtk.ListViewSelectionListener;
import org.apache.pivot.wtk.PushButton;

import at.fhv.audioracer.client.player.IServerDiscoverListener;
import at.fhv.audioracer.client.player.PlayerClient;
import at.fhv.audioracer.client.player.ServerDiscover;
import at.fhv.audioracer.ui.util.pivot.PivotThreadProxy;

public class ServerDiscoveryForm extends Form implements Bindable, IServerDiscoverListener {
	
	@BXML
	private ListView _listView;
	@BXML
	private PushButton _pushButton;
	
	private ServerDiscover _serverDiscover;
	private List<String> _serverList;
	
	public ServerDiscoveryForm() {
		_serverDiscover = new ServerDiscover();
		_serverDiscover.getListenerList().add(
				(IServerDiscoverListener) new PivotThreadProxy(this).getProxy());
		_serverList = new LinkedList<String>();
	}
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		_listView.setListData(_serverList);
		_listView.getListViewSelectionListeners().add(new ListViewSelectionListener.Adapter() {
			@Override
			public void selectedItemChanged(ListView listView, Object previousSelectedItem) {
				_pushButton.setEnabled(listView.getSelectedIndex() >= 0);
			}
		});
		
		_pushButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				PlayerClient client = PlayerSimulatorWindow.getInstance().getPlayerClient();
				try {
					String serverUrl = (String) _listView.getSelectedItem();
					client.startClient(PlayerSimulatorWindow.getInstance().getPlayerName(),
							serverUrl);
					
					client.setServerUrl(serverUrl);
					client.getPlayer().setName(PlayerSimulatorWindow.getInstance().getPlayerName());
					
					PlayerSimulatorWindow.getInstance().setContent(CarSelectForm.class);
					_serverDiscover.stopDiscover();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		_pushButton.setEnabled(false);
		
		_serverDiscover.start();
	}
	
	@Override
	public void onServerDiscovered(String host) {
		_serverList.add(host);
	}
	
	@Override
	public void onServerLost(String host) {
		_serverList.remove(host);
	}
}
