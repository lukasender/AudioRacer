package at.fhv.audioracer.server.dao;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.server.proxy.PlayerCommunicationProxy;

public class Player {
	
	private String _loginName;
	
	/**
	 * indicates player ready for next game nor not
	 */
	private boolean _isReady;
	
	/**
	 * server - player communication
	 */
	private PlayerCommunicationProxy _proxy;
	
	/**
	 * car associated with player
	 */
	private Car _car;
	
	public String getLoginName() {
		return _loginName;
	}
	
	public void setLoginName(String loginName) {
		this._loginName = loginName;
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
	
	public PlayerCommunicationProxy getProxy() {
		return _proxy;
	}
	
	public void setProxy(PlayerCommunicationProxy proxy) {
		_proxy = proxy;
	}
}
