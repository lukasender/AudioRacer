package at.fhv.audioracer.server.dao;

import at.fhv.audioracer.server.wrappers.Direction;
import at.fhv.audioracer.server.wrappers.Position;

public class Car {
	
	private Position position;
	private Direction direction;
	private byte[] image;
	
	public Position getPosition() {
		return position;
	}
	public void setPosition(Position position) {
		this.position = position;
	}
	public Direction getDirection() {
		return direction;
	}
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	public byte[] getImage() {
		return image;
	}
	public void setImage(byte[] image) {
		this.image = image;
	}
}
