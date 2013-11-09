package at.fhv.audioracer.simulator.player;

import java.io.IOException;
import java.net.InetAddress;

import at.fhv.audioracer.communication.player.PlayerNetwork;
import at.fhv.audioracer.communication.player.message.ConnectRequestMessage;
import at.fhv.audioracer.communication.player.message.ConnectResponseMessage;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class Main {
	
	public static void main(String[] args) {
		try {
			startClient();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static Client client;
	
	private static void startClient() throws IOException {
		// test purpose
		
		client = new Client();
		client.start();
		
		PlayerNetwork.register(client);
		
		client.addListener(new Listener() {
			public void received(Connection connection, Object object) {
				if (object instanceof ConnectResponseMessage) {
					ConnectResponseMessage resp = (ConnectResponseMessage) object;
					System.out.println("Connected ... server responsed with id: " + resp.playerId);
					connection.close();
					client.close();
				}
			}
		});
		client.connect(1000, InetAddress.getLoopbackAddress(), PlayerNetwork.PLAYER_SERVICE_PORT, PlayerNetwork.PLAYER_SERVICE_PORT);
		
		ConnectRequestMessage connectMsg = new ConnectRequestMessage();
		connectMsg.playerName = "Hello";
		System.out.println("Try to connect.");
		client.sendTCP(connectMsg);
		while (true) {
		}
	}
	
}
