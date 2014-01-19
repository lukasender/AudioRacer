package at.fhv.audioracer.camera.pivot;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import at.fhv.audioracer.ui.pivot.MapComponentSkin;

public class CameraMapComponentSkin extends MapComponentSkin {
	
	private final CameraMapComponent _component;
	
	private int _offsetX;
	private int _offsetY;
	private int _cameraWidth;
	private int _cameraHeight;
	
	public CameraMapComponentSkin(CameraMapComponent cameraMapComponent) {
		super(cameraMapComponent);
		
		_component = cameraMapComponent;
	}
	
	@Override
	protected int getMapX(int mapSizeX, float scale) {
		return _component.getCameraImagePosX() + (int) (_offsetX * scale);
	}
	
	@Override
	protected int getMapY(int mapSizeY, float scale) {
		return _component.getCameraImagePosY() + (int) (_offsetY * scale);
	}
	
	public void setMapOffset(int offsetX, int offsetY) {
		_offsetX = offsetX;
		_offsetY = offsetY;
	}
	
	@Override
	protected int getTotalWidth(int mapSizeX) {
		return _cameraWidth;
	}
	
	@Override
	protected int getTotalHeight(int mapSizeY) {
		return _cameraHeight;
	}
	
	@Override
	public void paint(Graphics2D graphics) {
		BufferedImage camera = _component.getCameraImage();
		if (camera != null) {
			graphics.drawImage(camera, _component.getCameraImagePosX(),
					_component.getCameraImagePosY(), _component.getCameraImageWidth(),
					_component.getCameraImageHeight(), null);
			
			_cameraWidth = camera.getWidth();
			_cameraHeight = camera.getHeight();
		}
		
		super.paint(graphics);
	}
}
