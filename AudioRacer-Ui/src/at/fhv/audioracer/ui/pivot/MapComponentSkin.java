package at.fhv.audioracer.ui.pivot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.util.Collection;

import org.apache.pivot.wtk.skin.ComponentSkin;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Checkpoint;
import at.fhv.audioracer.core.model.ConnectionState;
import at.fhv.audioracer.core.model.Map;
import at.fhv.audioracer.core.model.Player;

public class MapComponentSkin extends ComponentSkin {
	
	private final MapComponent _component;
	private final Color _defaultColor = new Color(0);
	private final float _imageDownScale = 0.02f;
	
	public MapComponentSkin(MapComponent mapComponent) {
		_component = mapComponent;
	}
	
	@Override
	public void layout() {
		// no-op.
	}
	
	@Override
	public int getPreferredWidth(int height) {
		return 0;
	}
	
	@Override
	public int getPreferredHeight(int width) {
		return 0;
	}
	
	protected int getMapX(int mapSizeX, float scale) {
		return (int) ((getWidth() - (mapSizeX * scale)) * 0.5);
	}
	
	protected int getMapY(int mapSizeY, float scale) {
		return (int) ((getHeight() - (mapSizeY * scale)) * 0.5);
	}
	
	protected int getTotalWidth(int mapSizeX) {
		return ((_component.getBorderSize() * 2) + mapSizeX);
	}
	
	protected int getTotalHeight(int mapSizeY) {
		return ((_component.getBorderSize() * 2) + mapSizeY);
	}
	
	private int _debugCtr = 0;
	
	@Override
	public void paint(Graphics2D graphics) {
		
		Map map = _component.getMap();
		if (map == null) {
			return;
		}
		
		int mapSizeX = map.getSizeX();
		int mapSizeY = map.getSizeY();
		
		float scaleWidth = (float) getWidth() / (float) getTotalWidth(mapSizeX);
		float scaleHeight = (float) getHeight() / (float) getTotalHeight(mapSizeY);
		
		float scale = Math.min(scaleWidth, scaleHeight);
		
		int mapX = getMapX(mapSizeX, scale);
		int mapY = getMapY(mapSizeY, scale);
		int mapWidth = (int) Math.floor(mapSizeX * scale);
		int mapHeight = (int) Math.floor(mapSizeY * scale);
		graphics.setColor(_defaultColor);
		graphics.drawRect(mapX, mapY, mapWidth, mapHeight);
		
		if (_debugCtr++ % 1000 == 0) {
			// System.out.println("mapX: " + mapX + " mapY: " + mapY + " scale: " + scale
			// + " mapSizeX: " + mapSizeX + " mapSizeY: " + mapSizeY + " scaleWidth: "
			// + scaleWidth + " scaleHeight: " + scaleHeight + " mapWidth: " + mapWidth
			// + " mapHeight: " + mapHeight);
		}
		
		for (Car<?> car : _component.getMap().getCars()) {
			AffineTransform xform = new AffineTransform();
			xform.translate(mapX + (car.getPosition().getPosX() * scale), mapY
					+ (car.getPosition().getPosY() * scale));
			xform.scale(_imageDownScale * scale, _imageDownScale * scale);
			xform.translate(-car.getImage().getWidth() / 2, -car.getImage().getHeight() / 2);
			xform.rotate(Math.toRadians(car.getDirection().getDirection()), car.getImage()
					.getWidth() / 2, car.getImage().getHeight() / 2);
			graphics.drawImage(car.getImage(), xform, null);
			
			Player p = (Player) car.getPlayer();
			float x = mapX + (car.getPosition().getPosX() * scale);
			float y = mapY + (car.getPosition().getPosY() * scale);
			float carMaxEdge = Math.max(car.getImage().getWidth(), car.getImage().getHeight());
			x -= (int) (carMaxEdge * _imageDownScale * scale / 2);
			y -= (int) ((carMaxEdge * _imageDownScale * scale) / 2) + 6;
			if (p != null) {
				graphics.drawString(p.getName(), x, y);
				if (p.getConnectionState().equals(ConnectionState.RECONNECTING)) {
					graphics.setColor(p.getConnectionState().getColor());
					y += (int) ((carMaxEdge * _imageDownScale * scale / 2) * 2) + 20;
					graphics.drawString(p.getConnectionState().getDescription(), x, y);
					graphics.setColor(_defaultColor);
				}
			} else {
				graphics.drawString("I am free", x, y);
			}
		}
		
		Collection<Checkpoint> c = _component.getMap().getCheckpoints();
		// if (c == null) {
		// c = new ArrayList<Checkpoint>();
		// }
		// byte b = 0;
		// c.add(new Checkpoint(b, new Position(33.53742f, 35.255993f), 10.0f, 1));
		for (Checkpoint cp : c) {
			Shape circle = new Ellipse2D.Double(mapX
					+ (cp.getPosition().getPosX() * scale - (scale * cp.getRadius())), mapY
					+ (cp.getPosition().getPosY() * scale - (scale * cp.getRadius())),
					(scale * 2.0 * cp.getRadius()), (scale * 2.0 * cp.getRadius()));
			graphics.draw(circle);
			
			graphics.drawString("car-id: " + cp.getCarId() + " nr: " + cp.getCheckpointNumber(),
					mapX + cp.getPosition().getPosX() * scale, mapY + cp.getPosition().getPosY()
							* scale);
		}
	}
}
