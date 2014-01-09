package at.fhv.audioracer.communication.world;

import at.fhv.audioracer.core.util.IListener;

public interface ICarManagerListener extends IListener {
	
	public void onCarClientConnect(ICarClient carClient);
	
	public void onCarClientDisconnect(ICarClient carClient);
}
