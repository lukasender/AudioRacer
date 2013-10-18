package at.fhv.audioracer.ui.pivot;

import java.awt.EventQueue;

import javax.naming.OperationNotSupportedException;

import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Component;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.ICarListener;
import at.fhv.audioracer.core.model.IMapListener;
import at.fhv.audioracer.core.model.Map;

public class MapComponent extends Component implements IMapListener, ICarListener {
	
	private Map _map;
	
	public MapComponent() {
		setSkin(new MapComponentSkin(this));
		
		/* Usage example (should be made outside of this class):
		try {
			setMap(new Map(20, 30));
		} catch (OperationNotSupportedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try {
					Thread.sleep(1000);
					Car car = new Car(0, 10, 10, 0, ImageIO.read(MapComponent.class.getResource("car-red.png")));
					_map.addCar(car);
					Thread.sleep(1000);
					while (true) {
						car.updatePosition(15, 15, 90);
						Thread.sleep(1000);
						car.updatePosition(10, 20, 180);
						Thread.sleep(1000);
						car.updatePosition(5, 15, 270);
						Thread.sleep(1000);
						car.updatePosition(10, 10, 0);
						Thread.sleep(1000);
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();*/
	}

	public Map getMap() {
		return _map;
	}

	public void setMap(Map map) throws OperationNotSupportedException {
		if (_map != null) {
			throw new OperationNotSupportedException("Changing map is not supported!");
		}
		
		if (map != null) {
			_map = map;
			
			_map.getMapListenerList().add(this);
		}
	}
		
	public int getBorderSize() {
		if (_map == null){
			return 0;
		}
		
		return (int) Math.floor(Math.max(_map.getSizeX(), _map.getSizeY()) * 0.1);
	}

	@Override
	public void onMapSizeChanged() {
		repaint();
	}

	@Override
	public void onCarAdded(Car addedCar) {
		addedCar.getCarListenerList().add(this);
		repaint();
	}

	@Override
	public void onCarRemoved(Car removedCar) {
		removedCar.getCarListenerList().remove(this);
		repaint();
	}

	@Override
	public void onCarPositionChanged(Car car) {
		repaint();
	}
	
	@Override
	public void repaint(final int xArgument, final int yArgument, final int width, final int height, final boolean immediate) {
		if (EventQueue.isDispatchThread()) {
			super.repaint(xArgument, yArgument, width, height, immediate);
		} else {
			ApplicationContext.queueCallback(new Runnable() {
				@Override
				public void run() {
					MapComponent.super.repaint(xArgument, yArgument, width, height, immediate);
				}
			});
		}
	}
}
