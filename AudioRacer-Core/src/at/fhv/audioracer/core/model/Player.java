package at.fhv.audioracer.core.model;

public class Player {
	
	/**
	 * unique player on server
	 */
	private int _playerId;
	
	/**
	 * player name
	 */
	private String _name;
	
	/**
	 * time in milliseconds since game start
	 */
	private int _time;
	
	/**
	 * indicates number of coins to collect
	 */
	private int _coinsLeft;
	
	/**
	 * indicates player ready for next game nor not
	 */
	private boolean _isReady;
	
	/**
	 * car associated with player
	 */
	private Car _car;
	
	public String getName() {
		return _name;
	}
	
	public void setName(String playerName) {
		this._name = playerName;
	}
	
	public boolean isReady() {
		return _isReady;
	}
	
	public void setReady(boolean isReady) {
		this._isReady = isReady;
	}
	
	public Car getCar() {
		return _car;
	}
	
	public void setCar(Car car) {
		this._car = car;
	}
	
	public int getPlayerId() {
		return _playerId;
	}
	
	public void setPlayerId(int playerId) {
		_playerId = playerId;
	}
	
	public int getTime() {
		return _time;
	}
	
	public void setTime(int time) {
		_time = time;
	}
	
	public int getCoinsLeft() {
		return _coinsLeft;
	}
	
	public void setCoinsLeft(int coinsLeft) {
		_coinsLeft = coinsLeft;
	}
	
}
