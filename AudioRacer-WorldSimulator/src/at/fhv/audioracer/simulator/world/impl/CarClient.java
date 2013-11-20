package at.fhv.audioracer.simulator.world.impl;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.util.Direction;
import at.fhv.audioracer.core.util.Position;

public class CarClient implements ICarClient {
	
	private Car _car = null;
	
	/**
	 * 
	 */
	private static final float MAX_FULL_LOCK = 4;
	
	private static final float SPEED_FACTOR = 1;
	
	public void setCar(Car car) {
		if (car == null) {
			throw new IllegalArgumentException("Car must not be null");
		}
		_car = car;
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		// get current position and direction
		Position currentPosition = _car.getPosition();
		
		/*
		 * Since the car's position is stored as absolute angles (0-360°), we can just add or subtract a relative value.
		 */
		Direction newDirection = calcNewDirection(direction, _car.getDirection());
		Position newPosition = calcNewPosition(speed, currentPosition, newDirection);
		
		_car.updatePosition(newPosition, newDirection);
	}
	
	/**
	 * 
	 * @param direction
	 *            given direction (-1 <= direction <= 1)
	 * @param currentDirection
	 *            current direction in which the car is positioned.
	 * @return
	 */
	private Direction calcNewDirection(float direction, Direction currentDirection) {
		float deltaAngle = direction * MAX_FULL_LOCK;
		float newDirection = currentDirection.getDirection() + deltaAngle;
		return new Direction(newDirection);
	}
	
	private Position calcNewPosition(float speed, Position currentPosition, Direction currentDirection) {
		// @formatter:off
		/*
		             /|
			     v  / |
			       /  | b
		     alpha----+
			curPos  a
			  	  
		 	v = speed * factor
			alpha = currentDirection in radians
			
			sin(alpha) = a/v
			cos(alpha) = b/v
			
			a = v * sin(alpha)
			b = v * cos(alpha)
			
			newPos = pos.x + b, pos.y + a
			@formatter:on
		 */
		// @formatter:on
		
		float x = currentPosition.getPosX();
		float y = currentPosition.getPosY();
		
		double alpha = Math.toRadians(currentDirection.getDirection());
		float v = speed * SPEED_FACTOR;
		
		double a = v * Math.sin(alpha);
		double b = v * Math.cos(alpha);
		
		x += (float) b;
		y += (float) a;
		
		return new Position(x, y);
	}
	
}
