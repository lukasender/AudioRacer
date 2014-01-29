package at.fhv.audioracer.client.android.activity.thread;

import android.view.View;
import android.widget.Button;
import at.fhv.audioracer.client.android.activity.PlayGameActivity;
import at.fhv.audioracer.client.android.activity.util.PressedButton;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.TrimSettingsAsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;

public class TrimSettingsControlThread extends ControlThread {
	
	private static float TRIM_STEP = 0.1f; // increase trim settings by TRIM_STEP per sec.
	private PressedButton _trimSpeedUp;
	private PressedButton _trimSpeedDown;
	private PressedButton _trimSteeringUp;
	private PressedButton _trimSteeringDown;
	
	private float _trimSpeed;
	private float _trimSteering;
	
	private PlayGameActivity _activity;
	
	public TrimSettingsControlThread(PlayGameActivity activity, PressedButton trimSpeedUp, PressedButton trimSpeedDown, PressedButton trimSteeringUp,
			PressedButton trimSteeringDown, Button tsSetTrimButton) {
		super();
		_maxControlWait = 10;
		
		_activity = activity;
		
		_trimSpeedUp = trimSpeedUp;
		_trimSpeedDown = trimSpeedDown;
		_trimSteeringUp = trimSteeringUp;
		_trimSteeringDown = trimSteeringDown;
		
		tsSetTrimButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setTrim();
			}
		});
	}
	
	@Override
	protected void reset() {
		_trimSpeedUp.pressed = false;
		_trimSpeedDown.pressed = false;
		_trimSteeringUp.pressed = false;
		_trimSteeringDown.pressed = false;
		_trimSpeed = 0.0f;
		_trimSteering = 0.0f;
	}
	
	@Override
	public void control() {
		float sensity = (((System.currentTimeMillis() - _lastUpdate) / 1000.f) * TRIM_STEP);
		if (_trimSpeedUp.pressed) {
			trimSpeedUp(sensity);
		}
		if (_trimSpeedDown.pressed) {
			trimSpeedDown(sensity);
		}
		if (_trimSteeringUp.pressed) {
			trimSteeringUp(sensity);
		}
		if (_trimSteeringDown.pressed) {
			trimSteeringDown(sensity);
		}
		
		ClientManager.getInstance().getPlayerClient().getPlayerServer().updateVelocity(_trimSpeed, _trimSteering);
	}
	
	private void trimSpeedUp(float trimStep) {
		_trimSpeed = Math.min(1.0f, _trimSpeed + trimStep);
		_activity.setTrimSpeedValue(_trimSpeed);
	}
	
	private void trimSpeedDown(float trimStep) {
		_trimSpeed = Math.max(-1.0f, _trimSpeed - trimStep);
		_activity.setTrimSpeedValue(_trimSpeed);
	}
	
	private void trimSteeringUp(float trimStep) {
		_trimSteering = Math.min(1.0f, _trimSteering + trimStep);
		_activity.setTrimSteeringValue(_trimSteering);
	}
	
	private void trimSteeringDown(float trimStep) {
		_trimSteering = Math.max(-1.0f, _trimSteering - trimStep);
		_activity.setTrimSteeringValue(_trimSteering);
	}
	
	private void setTrim() {
		_trimSpeed = 0.0f;
		_trimSteering = 0.0f;
		_activity.setTrimSpeedValue(_trimSpeed);
		_activity.setTrimSteeringValue(_trimSteering);
		
		new TrimSettingsAsyncTask().execute(new NetworkParams());
	}
	
}