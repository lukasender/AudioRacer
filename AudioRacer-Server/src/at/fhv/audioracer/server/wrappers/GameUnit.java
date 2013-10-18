package at.fhv.audioracer.server.wrappers;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.server.dao.Player;

public class GameUnit {
	private Player _player = null;
	private Car _car = null;
	
	public Player getPlayer() {
		return _player;
	}
	
	public void setPlayer(Player player) {
		this._player = player;
	}
	
	public Car getCar() {
		return _car;
	}
	
	public void setCar(Car car) {
		_car = car;
	}
	
}
