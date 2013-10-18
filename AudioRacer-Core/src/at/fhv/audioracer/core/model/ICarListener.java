package at.fhv.audioracer.core.model;

import at.fhv.audioracer.core.util.IListener;

public interface ICarListener extends IListener {
	
	public void onCarPositionChanged(Car car);
	
}
