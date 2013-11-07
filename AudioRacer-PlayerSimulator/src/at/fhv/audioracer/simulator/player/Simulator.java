package at.fhv.audioracer.simulator.player;

import at.fhv.audioracer.client.player.PlayerClient;
import at.fhv.audioracer.simulator.gui.ControlView;
import at.fhv.audioracer.simulator.gui.StartUpView;

public class Simulator {
	
	public static void main(String[] args) {
		
		PlayerClient pc = new PlayerClient();
		StartUpView suv = new StartUpView(pc);
		ControlView cv = new ControlView(pc);
		suv.setVisible(true);
		cv.setVisible(true);
		
	}
	
}
