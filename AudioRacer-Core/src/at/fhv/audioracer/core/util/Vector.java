package at.fhv.audioracer.core.util;

/**
 * This class represents a 2D vector every method coudl also be implemented for an nD-Vector
 * 
 * @author Stephan 12.02.2012
 */
public class Vector {
	
	protected float[] _values;
	private int _dimension;
	
	public Vector(float[] values) {
		_dimension = values.length;
		_values = new float[_dimension];
		for (int i = 0; i < _dimension; i++) {
			_values[i] = values[i];
		}
	}
	
	public Vector add(Vector vector) {
		if (getDimension() != vector.getDimension()) {
			throw new IllegalArgumentException("Dimension mismatch.");
		}
		float[] temp = new float[getDimension()];
		for (int i = 0; i < getDimension(); i++) {
			temp[i] = getValues()[i] + vector.getValues()[i];
		}
		return new Vector(temp);
	}
	
	public Vector subtract(Vector vector) {
		if (getDimension() != vector.getDimension()) {
			throw new IllegalArgumentException("Dimension mismatch.");
		}
		float[] temp = new float[getDimension()];
		for (int i = 0; i < getDimension(); i++) {
			temp[i] = getValues()[i] - vector.getValues()[i];
		}
		return new Vector(temp);
	}
	
	public Vector scalarMultiplication(float scalar) {
		float[] temp = new float[getDimension()];
		for (int i = 0; i < getDimension(); i++) {
			temp[i] = getValues()[i] * scalar;
		}
		return new Vector(temp);
		
	}
	
	public float getLength() {
		float temp = 0;
		for (float value : getValues()) {
			temp = temp + value * value;
		}
		return (float) Math.sqrt(temp);
	}
	
	public Vector norm() {
		return this.scalarMultiplication(1 / getLength());
	}
	
	public float[] getValues() {
		return _values;
	}
	
	/**
	 * @return the _dimension
	 */
	public int getDimension() {
		return _dimension;
	}
}
