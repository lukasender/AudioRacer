package at.fhv.audioracer.core.model;

public class Player {

	public static final int INVALID_PLAYER_ID = -1;

	/**
	 * unique player on server
	 */
	protected int _playerId = INVALID_PLAYER_ID;

	/**
	 * player name
	 */
	protected String _name;

	/**
	 * time in milliseconds since game start
	 */
	protected int _time;

	/**
	 * indicates number of coins to collect
	 */
	protected int _coinsLeft;

	/**
	 * Indicates player ready for next game nor not. Default is false.
	 */
	protected boolean _isReady = false;

	/**
	 * car associated with player
	 */
	protected Car<?> _car;

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

	public Car<?> getCar() {
		return _car;
	}

	public void setCar(Car<?> car) {
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

	@Override
	public String toString() {
		StringBuilder b = new StringBuilder();
		b.append("Player -> id: ");
		b.append(_playerId);
		b.append(" name: ");
		b.append(_name);
		b.append(" is ready: ");
		b.append(_isReady);
		b.append(" coinsLeft: ");
		b.append(_coinsLeft);
		b.append(" time: ");
		b.append(_time);
		if (_car != null) {
			b.append(" car-id: ");
			b.append(_car.getCarId());
		}
		return b.toString();
	}

}
