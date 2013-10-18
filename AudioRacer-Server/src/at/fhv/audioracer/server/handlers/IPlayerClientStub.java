package at.fhv.audioracer.server.handlers;

import at.fhv.audioracer.communication.player.IPlayerClient;

public class IPlayerClientStub implements IPlayerClient {

	@Override
	public void updateGameState(int playerId, int coinsLeft, int time) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerConnected(int playerId, String playerName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void playerDisconnected(int playerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateCheckpointDirection(float directionX, float directionY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateFreeCars(int[] carIds) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void gameOver() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void invalidCommand() {
		// TODO Auto-generated method stub
		
	}
	
}
