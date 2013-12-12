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
	
	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof GameInfo)) {
			return false;
		}
		
		HostInfo hi = (HostInfo) o;
		if (_address == null && hi._address == null) {
			return true;
		}
		
		return _address != null && hi._address != null && _address.equals(hi._address);
	}
}
