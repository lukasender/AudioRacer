package at.fhv.audioracer.server.dao;

import at.fhv.audioracer.server.proxy.PlayerCommunicationProxy;

public class Player extends at.fhv.audioracer.core.model.Player {
	
	// TODO Edi bitte Überleg dir einen besseren Namen wegen besserer Unterscheidbarkeit zu model.car
	
	/**
	 * server - player communication
	 */
	private PlayerCommunicationProxy _proxy;
	
	public PlayerCommunicationProxy getProxy() {
		return _proxy;
	}
	
	public void setProxy(PlayerCommunicationProxy proxy) {
		_proxy = proxy;
	}
}
