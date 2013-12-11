package at.fhv.audioracer.client.android.network;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import android.os.AsyncTask;
import at.fhv.audioracer.client.android.info.GameInfo;
import at.fhv.audioracer.client.android.info.HostInfo;

import com.esotericsoftware.kryonet.Client;

public class DiscoverHostTask extends AsyncTask<Integer, Integer, List<GameInfo>> {
	
	@Override
	protected List<GameInfo> doInBackground(Integer... ports) {
		List<GameInfo> games = new ArrayList<GameInfo>();
		for (int port : ports) {
			Client client = ClientManager.getInstance().getClient();
			client.start();
			InetAddress address = client.discoverHost(port, ClientManager.TIMEOUT);
			HostInfo host = new HostInfo(address);
			games.add(new GameInfo(address.getHostName(), address.getHostAddress(), host));
		}
		return games;
	}
}
