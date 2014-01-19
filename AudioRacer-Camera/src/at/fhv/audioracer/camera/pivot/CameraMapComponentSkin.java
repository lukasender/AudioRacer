package at.fhv.audioracer.camera.pivot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import at.fhv.audioracer.ui.pivot.MapComponentSkin;

public class CameraMapComponentSkin extends MapComponentSkin {
	
	private final CameraMapComponent _component;
	
	public CameraMapComponentSkin(CameraMapComponent cameraMapComponent) {
		super(cameraMapComponent);
		
		_component = cameraMapComponent;
	}
	
	@Override
	public void paint(Graphics2D graphics) {
		BufferedImage camera = _component.getCameraImage();
		if (camera != null) {
			graphics.drawImage(camera, _component.getCameraImagePosX(),
					_component.getCameraImagePosY(), _component.getCameraImageWidth(),
					_component.getCameraImageHeight(), null);
		}
		
		super.paint(graphics);
	}
}
