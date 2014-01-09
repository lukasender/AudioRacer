package at.fhv.audioracer.server.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.core.util.Position;

public class CheckpointUtil {
	
	private Logger _logger = LoggerFactory.getLogger(CheckpointUtil.class);
	private int _mapSizeX = 0;
	private int _mapSizeY = 0;
	private float _minVectorLength = 0;
	private int _minVectorLengthMapRatio = 2;
	private float _maxVectorLength = 0;
	private float _checkpointRadius = 0;
	
	public void setMapSize(int mapX, int mapY) {
		_mapSizeX = mapX;
		_mapSizeY = mapY;
		_minVectorLength = (float) Math.min(mapX, mapY) / _minVectorLengthMapRatio;
		_maxVectorLength = (float) (Math.min(mapX, mapY)) / 2;
		_checkpointRadius = (float) (Math.min(mapX, mapY)) / 10;
		
		_logger.debug("mapSizeX: {} mapSizeY: {} _minVectorLength: {} maxVectorLength: {}",
				new Object[] { _mapSizeX, _mapSizeY, _minVectorLength, _maxVectorLength });
	}
	
	public Position generateNextCheckpoint(Position currentPosition) {
		return generateNextCheckpoint(currentPosition, _minVectorLength);
	}
	
	public Position generateNextCheckpoint(Position currentPosition, float vectorLength) {
		
		float x = currentPosition.getPosX();
		float y = currentPosition.getPosY();
		
		float xNew = 0;
		float yNew = 0;
		
		// could be improved
		while (true) {
			double alpha = Math.toRadians(Math.random() * 360);
			double a = vectorLength * Math.sin(alpha);
			double b = vectorLength * Math.cos(alpha);
			
			xNew = x + (float) b;
			yNew = y + (float) a;
			if (xNew > 0 && yNew > 0 && xNew < _mapSizeX && yNew < _mapSizeY) {
				break;
			}
		}
		Position p = new Position(xNew, yNew);
		_logger.debug(
				"Initial Position x: {} y: {}, new Position x: {} y: {} ... vectorLenght:  {}",
				new Object[] { x, y, xNew, yNew, vectorLength });
		return p;
	}
	
	public boolean checkpointMatch(Position currentPosition, Position checkpoint) {
		return Math.pow((currentPosition.getPosX() - checkpoint.getPosX()), 2)
				+ Math.pow(currentPosition.getPosY() - checkpoint.getPosY(), 2) < Math.pow(
				_checkpointRadius, 2);
	}
	
	public float generateRandomVectorLength() {
		float rand = _minVectorLength
				+ (float) (Math.random() * ((_maxVectorLength - _minVectorLength) + 1));
		_logger.debug("next random generated vector length: {}", rand);
		return rand;
	}
	
	public float getCheckpointRadius() {
		return _checkpointRadius;
	}
	
	/**
	 * Uses Pythagorean Theorem to find shortest distance between two Positions.
	 * 
	 * @param posA
	 * @param posB
	 * @return the distance between the two Positions
	 */
	public float getDistance(Position posA, Position posB) {
		
		double x = (double) (Math.max(posA.getPosX(), posB.getPosX()) - Math.min(posA.getPosX(),
				posB.getPosX()));
		double y = (double) (Math.max(posA.getPosY(), posB.getPosY()) - Math.min(posA.getPosY(),
				posB.getPosY()));
		
		return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	/**
	 * Rotates the Position counter-clockwise through given rotationAngle.
	 * 
	 * @param pos
	 *            Position to rotate
	 * @param rotationAngle
	 *            angle to rotate in degrees
	 * @return
	 */
	public Position rotatePosition(Position pos, float rotationAngle) {
		return rotatePosition(pos, (double) rotationAngle);
	}
	
	public Position rotatePosition(Position pos, double rotationAngle) {
		double rotationAngleRad = Math.toRadians(rotationAngle);
		double x = pos.getPosX() * Math.cos(rotationAngleRad) - pos.getPosY() * Math.sin(rotationAngleRad);
		double y = pos.getPosX() * Math.sin(rotationAngleRad) + pos.getPosY() * Math.cos(rotationAngleRad);
		return new Position((float) x, (float) y);
	}
}
