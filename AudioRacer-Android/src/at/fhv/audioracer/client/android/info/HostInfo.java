package at.fhv.audioracer.client.android.info;

import java.net.InetAddress;

public class HostInfo {
	
	private InetAddress _address;
	
	public HostInfo(InetAddress address) {
		_address = address;
	}
	
	public InetAddress getInetAddress() {
		return _address;
	}
}
