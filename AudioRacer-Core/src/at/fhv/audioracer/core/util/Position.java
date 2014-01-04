package at.fhv.audioracer.core.util;


public class Position extends Vector {

	public Position(float x, float y) {
		super(new float[] { x, y });
	}

	public float getPosX() {
		return _values[0];
	}

	public void setPosX(float posX) {
		this._values[0] = posX;
	}

	public float getPosY() {
		return _values[1];
	}

	public void setPosY(float posY) {
		this._values[1] = posY;
	}

	@Override
	public String toString() {
		return "x: " + getPosX() + " y: " + getPosY();
	}
}
