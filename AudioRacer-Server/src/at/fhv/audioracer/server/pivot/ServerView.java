package at.fhv.audioracer.server.pivot;

import java.net.URL;
import java.util.Comparator;

import javax.naming.OperationNotSupportedException;

import jssc.SerialPortException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.LinkedList;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Alert;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Button;
import org.apache.pivot.wtk.ButtonPressListener;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.ListButton;
import org.apache.pivot.wtk.ListButtonSelectionListener;
import org.apache.pivot.wtk.PushButton;
import org.apache.pivot.wtk.TableView;
import org.apache.pivot.wtk.Window;

import at.fhv.audioracer.core.model.IMapListener;
import at.fhv.audioracer.serial.SerialInterface;
import at.fhv.audioracer.server.game.GameModerator;
import at.fhv.audioracer.ui.pivot.MapComponent;
import at.fhv.audioracer.ui.util.pivot.PivotThreadProxy;

public class ServerView extends Window implements Application, Bindable {
	
	@BXML
	protected MapComponent _map;
	@BXML
	private TableView _tableView;
	@BXML
	private ListButton _comPortsListButton;
	@BXML
	private PushButton _refreshComPortsButton;
	
	protected Window _window;
	
	@Override
	public void startup(Display display, Map<String, String> properties) throws Exception {
		BXMLSerializer bxml = new BXMLSerializer();
		_window = (Window) bxml.readObject(ServerView.class, "window.bxml");
		_window.open(display);
	}
	
	@Override
	public boolean shutdown(boolean optional) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public void suspend() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void resume() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		at.fhv.audioracer.core.model.Map map = new at.fhv.audioracer.core.model.Map(300, 300);
		try {
			_map.setMap(map);
			GameModerator.getInstance().setMap(map);
			
			List<Map<String, String>> list = new LinkedList<>();
			map.getMapListenerList().add(
					(IMapListener) new PivotThreadProxy(new ScoreBoardMapListener(map, list))
							.getProxy());
			list.setComparator(new Comparator<Map<String, String>>() {
				
				@Override
				public int compare(Map<String, String> o1, Map<String, String> o2) {
					if (!o1.containsKey("checkpoints") || !o2.containsKey("checkpoints")) {
						return 0;
					}
					
					String c1 = o1.get("checkpoints");
					String c2 = o2.get("checkpoints");
					
					return c1.compareTo(c2);
				}
			});
			_tableView.setTableData(list);
		} catch (OperationNotSupportedException e) {
			// we will notice this anyway
		}
		
		_comPortsListButton.getListButtonSelectionListeners().add(
				new ListButtonSelectionListener.Adapter() {
					private SerialInterface _serialInterface;
					
					@Override
					public void selectedItemChanged(ListButton listButton,
							Object previousSelectedItem) {
						try {
							if (_serialInterface != null) {
								_serialInterface.stop();
							}
							
							Object port = listButton.getSelectedItem();
							if (port != null) {
								_serialInterface = new SerialInterface(port.toString());
							}
						} catch (SerialPortException e) {
							Alert.alert(e.toString(), getWindow());
							e.printStackTrace();
						}
					}
				});
		_refreshComPortsButton.getButtonPressListeners().add(new ButtonPressListener() {
			
			@Override
			public void buttonPressed(Button button) {
				refreshComPorts();
			}
		});
		refreshComPorts();
	}
	
	private void refreshComPorts() {
		_comPortsListButton.setListData(new LinkedList<>(SerialInterface.getPortNames()));
	}
}
