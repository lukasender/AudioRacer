package at.fhv.audioracer.core.model;

import at.fhv.audioracer.core.util.Position;

public class Checkpoint {

	private int _carId;
	private Position _position;
	private float _radius;
	private float _EPSILON = 0.0001f;

	public Checkpoint(int carId, Position position, float radius) {
		_carId = carId;
		_position = position;
		_radius = radius;
	}

	public int getCarId() {
		return _carId;
	}

	public Position getPosition() {
		return _position;
	}

	public double getRadius() {
		return _radius;
	}

	public boolean equals(Object checkpoint) {
		if (checkpoint == null)
			return false;
		if (checkpoint == this)
			return true;
		if (!(checkpoint instanceof Checkpoint))
			return false;

		Checkpoint cPt = (Checkpoint) checkpoint;
		if (cPt.getCarId() == _carId
				&& nearlyEqual(cPt.getPosition().getPosX(),
						_position.getPosX(), _EPSILON)
				&& nearlyEqual(cPt.getPosition().getPosY(),
						_position.getPosY(), _EPSILON)) {
			return true;
		}
		return false;
	}

	private static boolean nearlyEqual(float a, float b, float epsilon) {
		final float absA = Math.abs(a);
		final float absB = Math.abs(b);
		final float diff = Math.abs(a - b);

		if (a == b) { // shortcut, handles infinities
			return true;
		} else if (a * b == 0) { // a or b or both are zero
			// relative error is not meaningful here
			return diff < (epsilon * epsilon);
		} else { // use relative error
			return diff / (absA + absB) < epsilon;
		}
	}
}
