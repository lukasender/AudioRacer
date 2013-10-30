package at.fhv.audioracer.server.dao;

import java.awt.image.BufferedImage;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.Direction;
import at.fhv.audioracer.core.model.Position;
import at.fhv.audioracer.server.proxy.CarCommunicationProxy;

/**
 * Subclass of core project Car.java
 * 
 * @author edi
 * 
 */
public class CarProxy extends Car {
	
	/**
	 * PlayerProxy associated to this CarProxy
	 */
	private PlayerProxy _player;
	
	/**
	 * server - car communication object of this CarProxy
	 */
	private CarCommunicationProxy _proxy;
	
	/**
	 * @param cardId
	 *            id assigned by Camera
	 */
	public CarProxy(int cardId) {
		super(cardId);
	}
	
	public CarProxy(int carId, Position position, Direction direction, BufferedImage image) {
		super(carId, position, direction, image);
	}
	
	/**
	 * @return The PlayerProxy associated to this CarProxy or null if currently <br/>
	 *         no PlayerProxy assigned
	 */
	public PlayerProxy getPlayer() {
		return _player;
	}
	
	/**
	 * @param player
	 *            PlayerProxy which should be assigned to this CarProxy
	 */
	public void setPlayer(PlayerProxy player) {
		_player = player;
	}
	
	/**
	 * @return CarCommunicationProxy used by this CarProxy for <br/>
	 *         server - car communication
	 */
	public CarCommunicationProxy getProxy() {
		return _proxy;
	}
	
	/**
	 * @param proxy
	 *            CarCommunicationProxy used by this CarProxy for <br/>
	 *            server - car communication
	 */
	public void setProxy(CarCommunicationProxy proxy) {
		_proxy = proxy;
	}
}
