package at.fhv.audioracer.simulator.player;

import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import at.fhv.audioracer.client.player.PlayerClient;
import at.fhv.audioracer.communication.player.IPlayerClientManager;
import at.fhv.audioracer.communication.player.PlayerNetwork;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class Main {
	
	public static void main(String[] args) {
		try {
			startClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void startClient() throws IOException {
		// test purpose
		
		// All method calls from kryonet will be made through this executor.
		// We are using a single threaded executor to ensure that everything is done on the same
		// thread we won't run in any cross threading problems.
		final Executor executor = Executors.newSingleThreadExecutor();
		
		Client client = new Client();
		client.start();
		
		// Register the classes that will be sent over the network.
		PlayerNetwork.register(client);
		
		// get the PlayerClientManager from the server
		IPlayerClientManager playerClientManager = ObjectSpace.getRemoteObject(client, PlayerNetwork.PLAYER_MANAGER, IPlayerClientManager.class);
		RemoteObject obj = (RemoteObject) playerClientManager;
		obj.setTransmitExceptions(false); // disable exception transmitting
		
		// create real PlayerClient
		PlayerClient playerClient = new PlayerClient();
		
		// register the PlayerClient to kryonet
		ObjectSpace objectSpace = new ObjectSpace(client);
		objectSpace.setExecutor(executor);
		objectSpace.register(PlayerNetwork.PLAYER_CLIENT, playerClient);
		
		client.connect(1000, InetAddress.getLoopbackAddress(), PlayerNetwork.PLAYER_SERVICE_PORT);
		
		int foo = playerClientManager.connect("hi!");
		System.out.println("foo is: " + foo);
		client.close();
	}
	
}
