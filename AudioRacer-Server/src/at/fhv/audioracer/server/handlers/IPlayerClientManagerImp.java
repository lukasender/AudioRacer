package at.fhv.audioracer.server.handlers;

import at.fhv.audioracer.communication.player.IPlayerClientManager;

public class IPlayerClientManagerImp implements IPlayerClientManager {
	
	@Override
	public int connect(String playerName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void disconnect(int playerId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateVelocity(int playerId, float speed, float direction) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean selectCar(int playerId, int carId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public byte[] getCarImage(int carId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setPlayerReady(int playerId) {
		// TODO Auto-generated method stub
		
	}
	
}
