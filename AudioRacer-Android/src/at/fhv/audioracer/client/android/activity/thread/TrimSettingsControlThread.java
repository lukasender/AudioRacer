package at.fhv.audioracer.client.android.activity.thread;

import at.fhv.audioracer.client.android.activity.PlayGameActivity;
import at.fhv.audioracer.client.android.activity.util.PressedButton;
import at.fhv.audioracer.client.android.controller.ClientManager;

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
			PressedButton trimSteeringDown) {
		super();
		_maxControlWait = 10;
		
		_activity = activity;
		
		_trimSpeedUp = trimSpeedUp;
		_trimSpeedDown = trimSpeedDown;
		_trimSteeringUp = trimSteeringUp;
		_trimSteeringDown = trimSteeringDown;
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
			_activity.trimSpeedUp(sensity);
		}
		if (_trimSpeedDown.pressed) {
			_activity.trimSpeedDown(sensity);
		}
		if (_trimSteeringUp.pressed) {
			_activity.trimSteeringUp(sensity);
		}
		if (_trimSteeringDown.pressed) {
			_activity.trimSteeringDown(sensity);
		}
		
		ClientManager.getInstance().getPlayerClient().getPlayerServer().updateVelocity(_trimSpeed, _trimSteering);
	}
	
}