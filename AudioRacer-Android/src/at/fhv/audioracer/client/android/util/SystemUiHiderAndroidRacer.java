package at.fhv.audioracer.client.android.util;

import android.app.Activity;
import android.view.View;

public class SystemUiHiderAndroidRacer extends SystemUiHiderBase {
	
	public SystemUiHiderAndroidRacer(Activity activity, View anchorView, int flags) {
		super(activity, anchorView, flags);
	}
	
	@Override
	public void show() {
		// do nothing
	}
	
}
