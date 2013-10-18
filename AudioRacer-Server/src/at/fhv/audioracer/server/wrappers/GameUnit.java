package at.fhv.audioracer.server.wrappers;

import at.fhv.audioracer.server.dao.Car;
import at.fhv.audioracer.server.dao.Player;

public class GameUnit {
	private Player player = null;
	private Car car = null;
	
	public Player getPlayer() {
		return player;
	}
	public void setPlayer(Player player) {
		this.player = player;
	}
	public Car getCar() {
		return car;
	}
	public void setCar(Car car) {
		this.car = car;
	}
	
	
}
