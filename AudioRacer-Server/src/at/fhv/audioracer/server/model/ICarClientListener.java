package at.fhv.audioracer.server.model;

import at.fhv.audioracer.communication.world.ICarClient;
import at.fhv.audioracer.core.util.IListener;

public interface ICarClientListener extends IListener {
	
	public void onCarClientConnect(ICarClient carClient);
	
	public void onCarClientDisconnect(ICarClient carClient);
}
