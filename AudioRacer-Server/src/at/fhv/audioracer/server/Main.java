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
		start(args);
	}
	
	public static void start(String[] args) {
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
			playerServer.bind(PlayerNetwork.PLAYER_SERVICE_PORT, PlayerNetwork.PLAYER_SERVICE_PORT);
			playerServer.start();
			
			// cameraServer = new Server();
			cameraServer = new Server(20 * 16384, 30 * 81920);
			cameraServer.bind(WorldNetwork.CAMERA_SERVICE_PORT);
			
			CameraServerListener cameraServerListener = new CameraServerListener(gameModerator);
			cameraServer.addListener(cameraServerListener);
			WorldNetwork.register(cameraServer);
			
			cameraServer.start();
			
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
