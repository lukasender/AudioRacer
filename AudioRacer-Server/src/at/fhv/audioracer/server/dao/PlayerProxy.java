package at.fhv.audioracer.server.dao;

import at.fhv.audioracer.core.model.Player;
import at.fhv.audioracer.server.proxy.PlayerCommunicationProxy;

/**
 * Subclass of core project Player.java
 * 
 * @author edi
 */
public class PlayerProxy extends Player {
	
	/**
	 * CarProxy associated with this PlayerProxy.
	 */
	private CarProxy _car;
	
	/**
	 * server - player communication object of this PlayerProxy
	 */
	private PlayerCommunicationProxy _proxy;
	
	/**
	 * @return PlayerCommunicationProxy used by this PlayerProxy for<br/>
	 *         server - player communication
	 */
	public PlayerCommunicationProxy getProxy() {
		return _proxy;
	}
	
	/**
	 * @param proxy
	 *            PlayerCommunicationProxy used by this PlayerProxy for<br/>
	 *            server - player communication
	 */
	public void setProxy(PlayerCommunicationProxy proxy) {
		_proxy = proxy;
	}
	
	/**
	 * @return CarProxy associated to this PlayerProxy or null <br/>
	 *         if no CarProxy assigned at the moment
	 */
	public CarProxy getCar() {
		return _car;
	}
	
	/**
	 * @param car
	 *            CarProxy which should be associated to this PlayerProxy
	 */
	public void setCar(CarProxy car) {
		_car = car;
	}
}
