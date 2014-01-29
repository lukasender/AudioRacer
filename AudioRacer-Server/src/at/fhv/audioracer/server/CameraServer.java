package at.fhv.audioracer.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Server;

public class CameraServer extends Server {
	
	private static Logger _logger = LoggerFactory.getLogger(CameraServer.class);
	
	private static CameraServer _cameraServer = null;
	
	private CameraServer() {
		super(20 * 16384, 30 * 81920);
	}
	
	public static CameraServer getInstance() {
		if (_cameraServer == null) {
			_cameraServer = new CameraServer();
		}
		return _cameraServer;
	}
	
	@Override
	protected Connection newConnection() {
		
		_logger.debug("new camera connection incoming");
		
		CameraConnection connection = new CameraConnection();
		return connection;
	}
}
