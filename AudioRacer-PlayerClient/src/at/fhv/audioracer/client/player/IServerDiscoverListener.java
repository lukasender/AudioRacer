package at.fhv.audioracer.client.player;

import at.fhv.audioracer.core.util.IListener;

public interface IServerDiscoverListener extends IListener {
	public void onServerDiscovered(String host);
	
	public void onServerLost(String host);
}
