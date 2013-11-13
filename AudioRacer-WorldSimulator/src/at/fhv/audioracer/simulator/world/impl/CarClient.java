package at.fhv.audioracer.simulator.world.impl;

import javax.vecmath.Vector2d;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;

public class CarClient implements ICarClient {
	
	private Car _car = null;
	
	public void setCar(Car car) {
		if (car == null) {
			throw new IllegalArgumentException("Car must not be null");
		}
		_car = car;
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		// get current position and direction
		Position pos = _car.getPosition();
		Direction d = _car.getDirection();
		
		Vector2d v = new Vector2d(pos.getPosX(), pos.getPosY());
		
		_car.updatePosition(pos, new Direction(direction));
	}
	
	public static class V2D extends Vector2d {
		
		public V2D(double x, double y) {
			super(x, y);
		}
		
		/**
		 * rotate by <code>angle</code>.
		 * 
		 * @param angle
		 *            in radians.
		 */
		public void rotate(double angle) {
			double x = (this.x * Math.cos(angle)) - (this.y * Math.sin(angle));
			double y = (this.x * Math.sin(angle)) + (this.y * Math.cos(angle));
			this.x = x;
			this.y = y;
		}
		
	}
	
}
