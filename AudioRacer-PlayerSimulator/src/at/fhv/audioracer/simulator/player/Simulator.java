package at.fhv.audioracer.simulator.player;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import at.fhv.audioracer.communication.player.IPlayerClientManager;
import at.fhv.audioracer.communication.player.PlayerNetwork;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class Simulator {
	
	// simulated direction between -1 (left) 1(right)
	private float direction;
	
	// simulated direction between -1(brake) 1(brake)
	private float acceleration;
	
	// simulated PlayerClient
	private static SimulatedPlayerClient _playerClient;
	
	// ClientManager
	private static IPlayerClientManager _playerClientManager;
	
	public static void main(String[] args) {
		String playerName = "Max Mustermann";
		int playerId = -1;
		boolean open = true;
		
		try {
			playerId = startClient(playerName);
			while (open) {
				
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private static int startClient(String playerName) throws IOException {
		// All method calls from kryonet will be made through this executor.
		// We are using a single threaded executor to ensure that everything is done on the same
		// thread we won't run in any cross threading problems.
		final Executor executor = Executors.newSingleThreadExecutor();
		
		Client client = new Client();
		client.start();
		
		// Register the classes that will be sent over the network.
		PlayerNetwork.register(client);
		
		// get the PlayerClientManager from the server
		IPlayerClientManager _playerClientManager = ObjectSpace.getRemoteObject(client, PlayerNetwork.PLAYER_MANAGER, IPlayerClientManager.class);
		RemoteObject obj = (RemoteObject) _playerClientManager;
		obj.setTransmitExceptions(false); // disable exception transmitting
		
		// create real PlayerClient
		_playerClient = new SimulatedPlayerClient();
		
		// register the PlayerClient to kryonet
		ObjectSpace objectSpace = new ObjectSpace(client);
		objectSpace.setExecutor(executor);
		objectSpace.register(PlayerNetwork.PLAYER_CLIENT, _playerClient);
		
		client.connect(1000, InetAddress.getLoopbackAddress(), PlayerNetwork.PLAYER_SERVICE_PORT);
		
		return _playerClientManager.connect(playerName);
		
	}
}
