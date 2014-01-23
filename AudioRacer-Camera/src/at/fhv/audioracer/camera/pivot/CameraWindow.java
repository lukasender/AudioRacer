package at.fhv.audioracer.camera.pivot;

import java.net.URL;

import org.apache.pivot.beans.BXML;
import org.apache.pivot.beans.BXMLSerializer;
import org.apache.pivot.beans.Bindable;
import org.apache.pivot.collections.Map;
import org.apache.pivot.util.Resources;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.FillPane;
import org.apache.pivot.wtk.Window;

public class CameraWindow extends Window implements Bindable {
	
	private static CameraWindow _instance;
	
	@BXML
	private FillPane _fillPane;
	
	public CameraWindow() {
		_instance = this;
	}
	
	public static CameraWindow getInstance() {
		return _instance;
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
