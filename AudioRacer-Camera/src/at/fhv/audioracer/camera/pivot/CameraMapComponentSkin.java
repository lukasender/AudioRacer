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
			int x = 0;
			int y = 0;
			int height = getHeight();
			int width = getWidth();
			
			float oversizeX = (float) camera.getWidth() / (float) width;
			float oversizeY = (float) camera.getHeight() / (float) height;
			
			if (oversizeX > oversizeY) {
				height = (int) (((float) width / (float) camera.getWidth()) * (float) camera
						.getHeight());
				y = (getHeight() - height) / 2;
			} else {
				width = (int) (((float) height / (float) camera.getHeight()) * (float) camera
						.getWidth());
				x = (getWidth() - width) / 2;
			}
			
			graphics.drawImage(camera, x, y, width, height, null);
		}
		
		super.paint(graphics);
	}
}
