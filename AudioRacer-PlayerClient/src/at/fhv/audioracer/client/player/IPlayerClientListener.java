package at.fhv.audioracer.client.player;

import at.fhv.audioracer.core.util.IListener;

public interface IPlayerClientListener extends IListener {
	public static class Adapter implements IPlayerClientListener {
		@Override
		public void onUpdateGameState(int playerId) {
			// no-op.
		}
		
		@Override
		public void onUpdateCheckpointDirection() {
			// no-op.
		}
		
		@Override
		public void onUpdateFreeCars() {
			// no-op.
		}
		
		@Override
		public void onPlayerConnected(int playerId) {
			// no-op.
		}
		
		@Override
		public void onPlayerDisconnected(int playerId) {
			// no-op.
		}
		
		@Override
		public void onGameStarts() {
			// no-op.
		}
	}
	
	void onUpdateGameState(int playerId);
	
	void onUpdateCheckpointDirection();
	
	void onUpdateFreeCars();
	
	void onPlayerConnected(int playerId);
	
	void onPlayerDisconnected(int playerId);
	
	void onGameStarts();
	
}
