package at.fhv.audioracer.serial;

import at.fhv.audioracer.core.util.IListener;

public interface ICarClientListener extends IListener {
	public abstract void onVelocityChanged(CarClient carClient);
}
