package at.fhv.audioracer.client.android.network;

import com.esotericsoftware.kryonet.Client;

public class ClientManager {
	
	private static ClientManager _manager;
	
	private Client _client;
	
	public static final int TIMEOUT = 5000; // ms
	
	private ClientManager() {
		_client = new Client();
	}
	
	public static ClientManager getInstance() {
		if (_manager == null) {
			_manager = new ClientManager();
		}
		
		return _manager;
	}
	
	public Client getClient() {
		return _client;
	}
	
}
