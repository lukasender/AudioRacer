package at.fhv.audioracer.client.player;

import at.fhv.audioracer.core.util.IListener;

public interface IPlayerClientListener extends IListener {
	
	void onUpdateGameState(int playerId);
	
	void onUpdateCheckpointDirection();
	
	void onUpdateFreeCars();
	
	void onPlayerConnected(int playerId);
	
	void onPlayerDisconnected(int playerId);
	
	void onGameStarts();
	
}
