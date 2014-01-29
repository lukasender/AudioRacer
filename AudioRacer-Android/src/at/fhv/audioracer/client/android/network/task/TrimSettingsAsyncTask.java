package at.fhv.audioracer.client.android.network.task;

import android.util.Log;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public class TrimSettingsAsyncTask extends NetworkAsyncTask<NetworkParams, Void> {
	
	@Override
	protected Void doInBackground(NetworkParams... params) {
		Log.d("AsyncTask", this.getClass().getName());
		ClientManager.getInstance().getPlayerClient().getPlayerServer().trim();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("AsyncTask", "onPostExecute" + this.getClass().getName());
		// TODO Auto-generated method stub
		super.onPostExecute(result);
	}
}
