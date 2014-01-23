package at.fhv.audioracer.client.android.activity.thread;

import at.fhv.audioracer.client.android.activity.util.PressedButton;
import at.fhv.audioracer.client.android.controller.ClientManager;

public class StandardControlThread extends ControlThread {
	
	private PressedButton _speedUp;
	private PressedButton _speedDown;
	private PressedButton _steerLeft;
	private PressedButton _steerRight;
	
	private static final float CONTROL_SENSITY = 1.f;
	
	private float _speed;
	private float _direction;
	
	public StandardControlThread(PressedButton speedUp, PressedButton speedDown, PressedButton steerLeft, PressedButton steerRight) {
		_speedUp = speedUp;
		_speedDown = speedDown;
		_steerLeft = steerLeft;
		_steerRight = steerRight;
	}
	
	@Override
	public void control() {
		/* copied from AudioRacer-PlayerSimulator: package at.fhv.audioracer.simulator.player.pivot.CarControlComponent */
		float sensity = (((System.currentTimeMillis() - _lastUpdate) / 250.f) * CONTROL_SENSITY);
		if (_speedUp.pressed) {
			_speed = Math.min(1.f, (_speed + sensity));
		} else if (_speedDown.pressed) {
			_speed = Math.max(-1.f, (_speed - sensity / 2.0f));
		} else if (_speed < 0) {
			_speed = Math.min(0.f, (_speed + sensity));
		} else if (_speed > 0) {
			_speed = Math.max(0.f, (_speed - sensity));
		}
		
		if (_steerLeft.pressed) {
			_direction = Math.max(-1.f, (_direction - sensity));
		} else if (_steerRight.pressed) {
			_direction = Math.min(1.f, (_direction + sensity));
		} else if (_direction < 0) {
			_direction = Math.min(0.f, (_direction + sensity));
		} else if (_direction > 0) {
			_direction = Math.max(0.f, (_direction - sensity));
		}
		/* end of: copied from AudioRacer-PlayerSimulator: package at.fhv.audioracer.simulator.player.pivot.CarControlComponent */
		
		// note that this is sent continuously
		ClientManager.getInstance().getPlayerClient().getPlayerServer().updateVelocity(_speed, _direction);
	}
	
	@Override
	protected void reset() {
		_speedUp.pressed = false;
		_speedDown.pressed = false;
		_steerLeft.pressed = false;
		_steerRight.pressed = false;
		_speed = 0.0f;
		_direction = 0.0f;
	}
	
}