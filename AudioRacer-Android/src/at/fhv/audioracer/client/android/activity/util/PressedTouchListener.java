package at.fhv.audioracer.client.android.activity.util;

import android.view.MotionEvent;
import android.view.View;

public class PressedTouchListener implements View.OnTouchListener {
	
	private volatile PressedButton pressed;
	
	public PressedTouchListener(PressedButton pressed) {
		super();
		this.pressed = pressed;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (MotionEvent.ACTION_DOWN == event.getAction()) {
			pressed.pressed = true;
		}
		if (MotionEvent.ACTION_UP == event.getAction()) {
			pressed.pressed = false;
		}
		
		return true;
	}
	
}