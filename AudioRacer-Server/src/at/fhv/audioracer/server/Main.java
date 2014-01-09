package at.fhv.audioracer.server;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.pivot.wtk.DesktopApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.communication.world.WorldNetwork;
import at.fhv.audioracer.server.game.GameModerator;
import at.fhv.audioracer.server.pivot.ServerView;
import at.fhv.audioracer.ui.util.awt.RepeatingReleasedEventsFixer;

import com.esotericsoftware.kryonet.Server;

public class Main {
	
	public static final Executor executor = Executors.newSingleThreadExecutor();
	private static Logger _logger = LoggerFactory.getLogger(Main.class);
	
	public static void main(String[] args) {
		start(args);
	}
	
	public static void start(String[] args) {
		PlayerServer playerServer = PlayerServer.getInstance();
		PlayerServerListener playerServerListener = null;
		Server cameraServer = null;
		
		try {
			// comment in next line for "test run"
			// SerialInterface sI = new SerialInterface("COM8");
			GameModerator gameModerator = GameModerator.getInstance();
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
			
			boolean displayGui = true;
			if (args != null) {
				for (String string : args) {
					if ("--gui=false".equals(string)) {
						displayGui = false;
						break;
					}
				}
			}
			
			if (displayGui) {
				new RepeatingReleasedEventsFixer().install();
				DesktopApplicationContext.main(ServerView.class, args);
			}
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
