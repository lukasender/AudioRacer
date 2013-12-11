package at.fhv.audioracer.simulator.player.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.Window;

import at.fhv.audioracer.client.player.PlayerClient;

public class PlayerSimulatorWindow extends Window implements Bindable {
	
	private static PlayerSimulatorWindow _instance;
	
	private String _playerName;
	
	private PlayerClient _playerClient;
	
	@BXML
	private FillPane _fillPane;
	
	public PlayerSimulatorWindow() {
		_instance = this;
		_playerClient = new PlayerClient();
	}
	
	public static PlayerSimulatorWindow getInstance() {
		return _instance;
	}
	
	public String getPlayerName() {
		return _playerName;
	}
	
	public void setPlayerName(String playerName) {
		_playerName = playerName;
	}
	
	public PlayerClient getPlayerClient() {
		return _playerClient;
	}
	
	@Override
	public void initialize(Map<String, Object> namespace, URL location, Resources resources) {
		// no-op.
	}
	
	public void setContent(Class<?> contentClass) {
		BXMLSerializer bxml = new BXMLSerializer();
		Component component;
		try {
			component = (Component) bxml.readObject(contentClass, contentClass.getSimpleName()
					+ ".bxml");
			_fillPane.removeAll();
			_fillPane.add(component);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
