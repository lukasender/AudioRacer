package at.fhv.audioracer.client.android.network.task;

import at.fhv.audioracer.client.android.activity.listener.IFreeCarsListener;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public class FreeCarsAsyncTask extends NetworkAsyncTask<NetworkParams, Boolean> {
	
	private IFreeCarsListener _listener;
	
	public FreeCarsAsyncTask(IFreeCarsListener listener) {
		_listener = listener;
	}
	
	@Override
	protected Boolean doInBackground(NetworkParams... params) {
		try {
			_listener.addCars(ClientManager.getInstance().getPlayerClient().getFreeCarIds());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
