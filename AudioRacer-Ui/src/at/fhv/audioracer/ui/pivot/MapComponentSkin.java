package at.fhv.audioracer.ui.pivot;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;

import org.apache.pivot.wtk.skin.ComponentSkin;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Map;

public class MapComponentSkin extends ComponentSkin {
	
	private final MapComponent _component;
	
	public MapComponentSkin(MapComponent mapComponent) {
		_component = mapComponent;
	}
	
	@Override
	public void layout() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public int getPreferredWidth(int height) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getPreferredHeight(int width) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public void paint(Graphics2D graphics) {
		Map map = _component.getMap();
		if (map == null) {
			return;
		}
		
		int mapSizeX = map.getSizeX();
		int mapSizeY = map.getSizeY();
		
		int width = getWidth();
		int height = getHeight();
		
		int border = _component.getBorderSize();
		
		float scaleWidth = (float) width / (float) ((border * 2) + mapSizeX);
		float scaleHeigth = (float) height / (float) ((border * 2) + mapSizeY);
		
		float scale = Math.min(scaleWidth, scaleHeigth);
		
		int mapX = (int) ((width - (mapSizeX * scale)) * 0.5);
		int mapY = (int) ((height - (mapSizeY * scale)) * 0.5);
		int mapWidth = (int) Math.floor(mapSizeX * scale);
		int mapHeight = (int) Math.floor(mapSizeY * scale);
		graphics.drawRect(mapX, mapY, mapWidth, mapHeight);
		
		for (Car car : _component.getMap().getCars()) {
			AffineTransform xform = new AffineTransform();
			xform.translate(mapX + (car.getPosX() * scale), mapY + (car.getPosY() * scale));
			xform.scale(0.1, 0.1);
			xform.translate(-car.getImage().getWidth() / 2, -car.getImage().getHeight() / 2);
			xform.rotate(Math.toRadians(car.getDirection()), car.getImage().getWidth() / 2, car.getImage().getHeight() / 2);
			graphics.drawImage(car.getImage(), xform, null);
		}
	}
}
