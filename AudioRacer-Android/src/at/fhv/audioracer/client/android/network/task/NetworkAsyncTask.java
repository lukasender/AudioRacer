package at.fhv.audioracer.client.android.network.task;

import android.os.AsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public abstract class NetworkAsyncTask<T extends NetworkParams> extends AsyncTask<T, Integer, Boolean> {
	
	@Override
	protected Boolean doInBackground(T... arg0) {
		throw new UnsupportedOperationException();
	}
	
}
