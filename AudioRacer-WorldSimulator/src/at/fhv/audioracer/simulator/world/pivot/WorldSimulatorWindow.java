package at.fhv.audioracer.simulator.world.pivot;

import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.collections.Map;
import org.apache.pivot.wtk.Application;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.DesktopApplicationContext;
import org.apache.pivot.wtk.Display;
import org.apache.pivot.wtk.Window;

import at.fhv.audioracer.ui.util.awt.RepeatingReleasedEventsFixer;

public class WorldSimulatorWindow implements Application {

	private Window _window;

	@Override
	public void startup(Display display, Map<String, String> properties) throws Exception {
		BXMLSerializer bxml = new BXMLSerializer();
		_window = (Window) bxml.readObject(WorldSimulatorWindow.class, "window.bxml");
		_window.open(display);

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
	public boolean shutdown(boolean optional) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setView(Component view) {
		_window.setContent(view);
	}

	public static void main(String[] args) {
		new RepeatingReleasedEventsFixer().install();
		DesktopApplicationContext.main(WorldSimulatorWindow.class, args);
	}

}
