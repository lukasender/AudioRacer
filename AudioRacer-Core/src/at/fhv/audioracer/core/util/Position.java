package at.fhv.audioracer.core.util;

public class Position {
	private float _posX;
	private float _posY;
	
	public Position(float x, float y) {
		_posX = x;
		_posY = y;
	}
	
	public float getPosX() {
		return _posX;
	}
	
	public void setPosX(float posX) {
		this._posX = posX;
	}
	
	public float getPosY() {
		return _posY;
	}
	
	public void setPosY(float posY) {
		this._posY = posY;
	}
}
