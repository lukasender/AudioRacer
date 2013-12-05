package at.fhv.audioracer.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.server.game.GameModerator;

import com.esotericsoftware.kryonet.Server;

public class Main {
	
	public static final Executor executor = Executors.newSingleThreadExecutor();
	private static Logger _logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		PlayerServer playerServer = null;
		PlayerServerListener playerServerListener = null;
		Server cameraServer = null;
		GameModerator gameModerator = null;
		
		try {
			playerServer = new PlayerServer();
			gameModerator = new GameModerator(playerServer);
			playerServerListener = new PlayerServerListener(gameModerator);
			playerServer.addListener(playerServerListener);
			
			PlayerNetwork.register(playerServer);
			int playerServicePort = PlayerNetwork.PLAYER_SERVICE_PORT;
			playerServer.bind(playerServicePort, playerServicePort);
			playerServer.start();
			_logger.info("PlayerNetwork: " + playerServicePort);
			
			// cameraServer = new Server();
			cameraServer = new Server(16384, 81920);
			int worldServicePort = WorldNetwork.CAMERA_SERVICE_PORT;
			cameraServer.bind(worldServicePort);
			
			CameraServerListener cameraServerListener = new CameraServerListener(gameModerator);
			cameraServer.addListener(cameraServerListener);
			WorldNetwork.register(cameraServer);
			
			cameraServer.start();
			_logger.info("WorldNetwork: " + worldServicePort);
			
		} catch (Exception e) {
			_logger.error("Exception caught during application startup.", e);
			
			if (playerServer != null) {
				playerServer.close();
			}
			if (cameraServer != null) {
				cameraServer.close();
			}
			// TODO: Are this all connections, we need to close?
		}
	}
}
