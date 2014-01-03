package at.fhv.audioracer.core.model;

import at.fhv.audioracer.core.util.IListener;

public interface IMapListener extends IListener {
	
	public void onMapSizeChanged();

	public void onCarAdded(Car addedCar);

	public void onCarRemoved(Car removedCar);

	public void onCheckpointChange();
}
