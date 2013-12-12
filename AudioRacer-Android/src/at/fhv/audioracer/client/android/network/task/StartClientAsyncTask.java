package at.fhv.audioracer.client.android.network.task;

import java.io.IOException;

import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.StartClientParams;
import at.fhv.audioracer.client.player.PlayerClient;

public class StartClientAsyncTask extends NetworkAsyncTask<StartClientParams, Boolean> {
	
	@Override
	protected Boolean doInBackground(StartClientParams... params) {
		StartClientParams p = params[0];
		PlayerClient player = ClientManager.getInstance().getPlayerClient();
		try {
			player.startClient(p.playerName, p.host);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
}
