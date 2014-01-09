package at.fhv.audioracer.client.android.network.task;

import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public class TrimSettingsAsyncTask extends NetworkAsyncTask<NetworkParams, Void> {
	
	@Override
	protected Void doInBackground(NetworkParams... params) {
		ClientManager.getInstance().getPlayerClient().getPlayerServer().trim();
		return null;
	}
	
}
