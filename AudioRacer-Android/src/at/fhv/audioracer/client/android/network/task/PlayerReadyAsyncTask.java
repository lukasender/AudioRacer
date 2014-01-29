package at.fhv.audioracer.client.android.network.task;

import android.util.Log;
import at.fhv.audioracer.client.android.activity.PlayGameActivity.ControlMode;
import at.fhv.audioracer.client.android.activity.listener.IControlMode;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public class PlayerReadyAsyncTask extends NetworkAsyncTask<NetworkParams, Void> {
	
	private IControlMode _imode;
	private ControlMode _mode;
	
	public PlayerReadyAsyncTask(IControlMode imode, ControlMode mode) {
		_imode = imode;
		_mode = mode;
	}
	
	@Override
	protected Void doInBackground(NetworkParams... params) {
		Log.d("AsyncTask", this.getClass().getName());
		ClientManager.getInstance().getPlayerClient().getPlayerServer().setPlayerReady();
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("AsyncTask", "onPostExecute" + this.getClass().getName());
		_imode.setControlMode(_mode);
	}
	
}
