package at.fhv.audioracer.client.android.network.task;

import android.os.AsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

/**
 * 
 * @author lumannnn
 * 
 * @param <T>
 *            The task parameters
 * @param <R>
 *            Result type
 */
public abstract class NetworkAsyncTask<T extends NetworkParams, R> extends AsyncTask<T, Integer, R> {
	
	@Override
	protected R doInBackground(T... arg0) {
		throw new UnsupportedOperationException();
	}
	
}
