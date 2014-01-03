package at.fhv.audioracer.client.player;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.core.util.ListenerList;

import com.esotericsoftware.kryonet.Client;

public class ServerDiscover extends Thread {
	private static class ServerDiscoverListenerList extends ListenerList<IServerDiscoverListener>
			implements IServerDiscoverListener {
		@Override
		public void onServerDiscovered(String host) {
			for (IServerDiscoverListener listener : listeners()) {
				listener.onServerDiscovered(host);
			}
		}
		
		@Override
		public void onServerLost(String host) {
			for (IServerDiscoverListener listener : listeners()) {
				listener.onServerLost(host);
			}
		}
	}
	
	private static final long MAX_SHOWN_DURATION = 3000;
	
	private volatile boolean _running;
	private HashMap<String, Long> _knownServers;
	
	private ServerDiscoverListenerList _listenerList;
	
	public ServerDiscover() {
		super("Server discover thread");
		
		_knownServers = new HashMap<String, Long>();
		_listenerList = new ServerDiscoverListenerList();
	}
	
	public ListenerList<IServerDiscoverListener> getListenerList() {
		return _listenerList;
	}
	
	public void stopDiscover() {
		_knownServers.clear();
		_running = false;
	}
	
	@Override
	public void run() {
		_running = true;
		Client client = new Client();
		while (_running) {
			List<InetAddress> servers = client.discoverHosts(PlayerNetwork.PLAYER_SERVICE_PORT,
					1000);
			for (InetAddress inetAddress : servers) {
				String host = inetAddress.getHostAddress();
				if (!_knownServers.containsKey(host)) {
					_listenerList.onServerDiscovered(host);
				}
				_knownServers.put(host, System.currentTimeMillis());
			}
			
			long minLastSeen = System.currentTimeMillis() - MAX_SHOWN_DURATION;
			for (String host : _knownServers.keySet().toArray(new String[_knownServers.size()])) {
				if (_knownServers.get(host) <= minLastSeen) {
					_knownServers.remove(host);
					_listenerList.onServerLost(host);
				}
			}
		}
	}
	
	public void clearCache() {
		_knownServers.clear();
	}
}
