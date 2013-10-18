package at.fhv.audioracer.server;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import at.fhv.audioracer.communication.player.IPlayerClient;
import at.fhv.audioracer.communication.player.PlayerNetwork;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class Main {
	
	public static void main(String[] args) {
		try {
			startServer();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void startServer() throws IOException {
		// Test purpose only
		
		// All method calls from kryonet will be made through this executor.
		// We are using a single threaded executor to ensure that everything is done on the same
		// thread we won't run in any cross threading problems.
		final Executor executor = Executors.newSingleThreadExecutor();
		
		// Create a new server
		Server server = new Server() {
			@Override
			protected Connection newConnection() {
				// called when a new incoming connection is pending
				
				// create a PlayerClientManager which gets calls from the client on the server side
				PlayerClientManager playerClientManager = new PlayerClientManager();
				ObjectSpace objectSpace = new ObjectSpace(playerClientManager);
				objectSpace.setExecutor(executor); // use the single threaded executor for method invocation
				objectSpace.register(PlayerNetwork.PLAYER_MANAGER, playerClientManager); // register the PlayerClientManager to kryonet
				
				// get the IPlayerClient which makes calls on the client side triggered by the server
				IPlayerClient playerClient = ObjectSpace.getRemoteObject(playerClientManager, PlayerNetwork.PLAYER_CLIENT, IPlayerClient.class);
				RemoteObject obj = (RemoteObject) playerClient;
				obj.setTransmitExceptions(false); // disable exception transmitting over the network (it seems that it doesn't work well)
				
				// tell PlayerClientManager the other part (playerClient)
				playerClientManager.setPlayerClient(playerClient);
				
				return playerClientManager;
			}
		};
		
		// Register the classes that will be sent over the network.
		PlayerNetwork.register(server);
		
		server.bind(4711);
		server.start();
	}
	
}
