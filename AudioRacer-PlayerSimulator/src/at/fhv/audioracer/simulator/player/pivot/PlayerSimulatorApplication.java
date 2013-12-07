package at.fhv.audioracer.simulator.player.pivot;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Application.UnprocessedKeyHandler;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Keyboard.KeyLocation;
import org.apache.pivot.wtk.Window;

public class PlayerSimulatorApplication implements Application, UnprocessedKeyHandler {
	
	private static PlayerSimulatorApplication _instance;
	
	private UnprocessedKeyHandler _keyHandler;
	
	public PlayerSimulatorApplication() {
		_instance = this;
	}
	
	public static PlayerSimulatorApplication getInstance() {
		return _instance;
	}
	
	public UnprocessedKeyHandler getKeyHandler() {
		return _keyHandler;
	}
	
	public void setKeyHandler(UnprocessedKeyHandler keyHandler) {
		_keyHandler = keyHandler;
	}
	
	@Override
	public void startup(Display display, Map<String, String> properties) throws Exception {
		System.out.println("startup()");
		BXMLSerializer bxml = new BXMLSerializer();
		Window window = (Window) bxml.readObject(PlayerSimulatorApplication.class,
				"PlayerSimulatorWindow.bxml");
		window.open(display);
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
	public void keyTyped(char character) {
		if (_keyHandler != null) {
			_keyHandler.keyTyped(character);
		}
	}
	
	@Override
	public void keyPressed(int keyCode, KeyLocation keyLocation) {
		if (_keyHandler != null) {
			_keyHandler.keyPressed(keyCode, keyLocation);
		}
	}
	
	@Override
	public void keyReleased(int keyCode, KeyLocation keyLocation) {
		if (_keyHandler != null) {
			_keyHandler.keyReleased(keyCode, keyLocation);
		}
	}
	
	public static void main(String[] args) {
		DesktopApplicationContext.main(PlayerSimulatorApplication.class, args);
	}
}
