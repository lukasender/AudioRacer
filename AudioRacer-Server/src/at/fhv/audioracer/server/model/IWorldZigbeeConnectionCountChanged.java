package at.fhv.audioracer.server.model;

import at.fhv.audioracer.core.util.IListener;

public interface IWorldZigbeeConnectionCountChanged extends IListener {
	/**
	 * Fires on each connection established between ZigBee and a Car.
	 * 
	 * @param oldValue
	 *            old connection count
	 * @param newValue
	 *            new connection count
	 */
	public void onWorldZigbeeConnectionCountChanged(int oldValue, int newValue);
}
