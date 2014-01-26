package at.fhv.audioracer.client.player;

import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.core.util.IListener;
import at.fhv.audioracer.core.util.Position;

public interface IPlayerClientListener extends IListener {
	public static class Adapter implements IPlayerClientListener {
		@Override
		public void onUpdateGameState(Player player) {
			// no-op.
		}
		
		@Override
		public void onUpdateCheckpointDirection(Position position) {
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
		
		@Override
		public void onGameEnd() {
			
		}
	}
	
	void onUpdateGameState(Player player);
	
	void onUpdateCheckpointDirection(Position position);
	
	void onUpdateFreeCars();
	
	void onPlayerConnected(int playerId);
	
	void onPlayerDisconnected(int playerId);
	
	void onGameStarts();
	
	void onGameEnd();
	
}
