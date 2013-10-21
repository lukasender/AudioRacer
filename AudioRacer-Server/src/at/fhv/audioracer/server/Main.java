package at.fhv.audioracer.server;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.IPlayerClient;
import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.server.proxy.CameraCommunicationProxy;
import at.fhv.audioracer.server.proxy.PlayerCommunicationProxy;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.kryonet.rmi.ObjectSpace;
import com.esotericsoftware.kryonet.rmi.RemoteObject;

public class Main {
	
	public static final Executor executor = Executors.newSingleThreadExecutor();
	private static Server _playerProxyServer = null;
	private static Server _cameraServer = null;
	private static Logger _logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		try {
			startServer();
		} catch (Exception e) {
			_logger.error("Exception caught during application startup.", e);
			
			if (_playerProxyServer != null) {
				_playerProxyServer.close();
			}
			if (_cameraServer != null) {
				_cameraServer.close();
			}
			// TODO: Are this all connections, we need to close?
		}
	}
	
	private static void startServer() throws IOException {
		// Test purpose only
		
		// Create new server for player communication
		_playerProxyServer = new Server() {
			@Override
			protected Connection newConnection() {
				// called when a new incoming device connection is pending
				
				// create a PlayerClientManager which gets calls from the client on the server side
				PlayerCommunicationProxy playerClientManager = new PlayerCommunicationProxy();
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
		PlayerNetwork.register(_playerProxyServer);
		
		_playerProxyServer.bind(PlayerNetwork.PLAYER_SERVICE_PORT);
		_playerProxyServer.start();
		
		// Create new Server for camera communication
		_cameraServer = new Server() {
			@Override
			protected Connection newConnection() {
				CameraCommunicationProxy cameraCommunicationProxy = new CameraCommunicationProxy();
				
				ObjectSpace objectSpace = new ObjectSpace(cameraCommunicationProxy);
				objectSpace.setExecutor(executor);
				objectSpace.register(WorldNetwork.CAMERA_SERVICE, cameraCommunicationProxy);
				
				return cameraCommunicationProxy;
			}
		};
		
		WorldNetwork.register(_cameraServer);
		
		_cameraServer.bind(WorldNetwork.CAMERA_SERVICE_PORT);
		_cameraServer.start();
	}
}
