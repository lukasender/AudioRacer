package at.fhv.audioracer.server.game;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.core.util.Position;

public class CheckpointUtil {
	
	private Logger _logger = LoggerFactory.getLogger(CheckpointUtil.class);
	private int _mapSizeX = 0;
	private int _mapSizeY = 0;
	private float _minVectorLength = 0;
	private int _minVectorLengthMapRatio = 6;
	private float _maxVectorLength = 0;
	
	public void setMapSize(int mapX, int mapY) {
		_mapSizeX = mapX;
		_mapSizeY = mapY;
		_minVectorLength = (float) Math.min(mapX, mapY) / _minVectorLengthMapRatio;
		_maxVectorLength = (float) (Math.min(mapX, mapY)) / 2;
		
		_logger.debug("mapSizeX: {} mapSizeY: {} _minVectorLength: {} maxVectorLength: {}",
				new Object[] { _mapSizeX, _mapSizeY, _minVectorLength, _maxVectorLength });
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
			if (xNew > 0 && yNew > 0) {
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
		return false;
	}
	
	public float generateRandomVectorLength() {
		float rand = _minVectorLength
				+ (float) (Math.random() * ((_maxVectorLength - _minVectorLength) + 1));
		_logger.debug("next random generated vector length: {}", rand);
		return rand;
	}
}
