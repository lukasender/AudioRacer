package at.fhv.audioracer.simulator.player.pivot;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pivot.wtk.skin.ComponentSkin;

import at.fhv.audioracer.core.util.Position;

public class CarControlComponentSkin extends ComponentSkin {
	
	private static final int CONTROL_SIZE = 100;
	private static final int CONTROL_PADDING = 10;
	
	private final CarControlComponent _component;
	
	private final BufferedImage _arrow;
	
	public CarControlComponentSkin(CarControlComponent carControlComponent) {
		_component = carControlComponent;
		try {
			_arrow = ImageIO.read(CarControlComponentSkin.class.getResource("arrow.png"));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
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
	
	@Override
	public void paint(Graphics2D graphics) {
		int width = getWidth();
		int height = getHeight();
		
		int controlYCenter = height - (CONTROL_SIZE / 2);
		int controlXCenter = (width / 2);
		
		int speedControlX = controlXCenter - (CONTROL_SIZE / 2);
		int speedControlY = height - CONTROL_SIZE + CONTROL_PADDING;
		graphics.setColor(Color.black);
		graphics.drawLine(speedControlX, speedControlY, speedControlX, (height - CONTROL_PADDING));
		graphics.drawLine((speedControlX - (CONTROL_PADDING * 2)), controlYCenter,
				(speedControlX + (CONTROL_PADDING * 2)), controlYCenter);
		
		int speedControlValueY = controlYCenter
				+ (int) (_component.getSpeed() * -1.f * ((CONTROL_SIZE / 2) - CONTROL_PADDING));
		graphics.setColor(Color.red);
		graphics.drawLine((speedControlX - CONTROL_PADDING), speedControlValueY,
				(speedControlX + CONTROL_PADDING), speedControlValueY);
		
		int directionControlX = (controlXCenter + CONTROL_PADDING);
		graphics.setColor(Color.black);
		graphics.drawLine(directionControlX, controlYCenter,
				(controlXCenter + CONTROL_SIZE - CONTROL_PADDING), controlYCenter);
		int directionControlCenter = (controlXCenter + (CONTROL_SIZE / 2));
		graphics.drawLine(directionControlCenter, (controlYCenter - (CONTROL_PADDING * 2)),
				directionControlCenter, (controlYCenter + (CONTROL_PADDING * 2)));
		
		int directionControlValueX = directionControlCenter
				+ (int) (_component.getDirection() * ((CONTROL_SIZE / 2) - CONTROL_PADDING));
		graphics.setColor(Color.red);
		graphics.drawLine(directionControlValueX, (controlYCenter - CONTROL_PADDING),
				directionControlValueX, (controlYCenter + CONTROL_PADDING));
		
		Position next = _component.getNextCheckPoint();
		if (next != null) {
			int arrowCenterX = (width - CONTROL_SIZE) / 2;
			int arrowCenterY = (height - CONTROL_SIZE) / 2;
			
			double distance = Math.sqrt(Math.pow(next.getPosX(), 2) + Math.pow(next.getPosY(), 2));
			double scale = 1;
			if (distance < 300.0) {
				scale = (distance / 300.0);
				scale = Math.max(0.01, scale);
			}
			
			AffineTransform xform = new AffineTransform();
			xform.translate(arrowCenterX, arrowCenterY);
			xform.scale(scale, scale);
			xform.translate(-_arrow.getWidth() / 2, -_arrow.getHeight() / 2);
			xform.rotate(next.getPosX(), next.getPosY() * -1, _arrow.getWidth() / 2,
					_arrow.getHeight() / 2);
			graphics.drawImage(_arrow, xform, null);
		}
	}
}
