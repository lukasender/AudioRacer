package at.fhv.audioracer.client.android.network.task;

import java.io.IOException;

import android.content.Intent;
import android.util.Log;
import at.fhv.audioracer.client.android.activity.JoinGameActivity;
import at.fhv.audioracer.client.android.activity.SelectCarActivity;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.StartClientParams;
import at.fhv.audioracer.client.player.PlayerClient;

public class StartClientAsyncTask extends NetworkAsyncTask<StartClientParams, Void> {
	
	private JoinGameActivity _joinGameActivity;
	
	public StartClientAsyncTask(JoinGameActivity joinGameActivity) {
		_joinGameActivity = joinGameActivity;
	}
	
	@Override
	protected Void doInBackground(StartClientParams... params) {
		Log.d("AsyncTask", this.getClass().getName());
		
		StartClientParams p = params[0];
		PlayerClient player = ClientManager.getInstance().getPlayerClient();
		try {
			player.startClient(p.playerName, p.host);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		Log.d("AsyncTask", "onPostExecute" + this.getClass().getName());
		
		final Intent selectCarsIntent = new Intent(_joinGameActivity, SelectCarActivity.class);
		_joinGameActivity.startActivity(selectCarsIntent);
	}
}
