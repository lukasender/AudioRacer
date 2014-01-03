package at.fhv.audioracer.server.pivot;

import java.net.URL;

import javax.naming.OperationNotSupportedException;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;

import at.fhv.audioracer.server.game.GameModerator;
import at.fhv.audioracer.ui.pivot.MapComponent;

public class ServerView extends Window implements Application, Bindable {
	
	@BXML
	protected MapComponent _map;
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
		} catch (OperationNotSupportedException e) {
			// we will notice this anyway
		}
	}
}
