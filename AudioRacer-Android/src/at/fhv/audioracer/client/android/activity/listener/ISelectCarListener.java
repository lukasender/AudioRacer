package at.fhv.audioracer.client.android.activity.listener;

import at.fhv.audioracer.client.android.network.task.SelectFreeCarAsyncTask.SuccessMessage;

public interface ISelectCarListener {
	
	public void notifySuccess(SuccessMessage message);
	
}
