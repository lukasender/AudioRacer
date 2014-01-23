package at.fhv.audioracer.client.android.activity.thread;

import at.fhv.audioracer.client.android.activity.view.JoystickView;
import at.fhv.audioracer.client.android.controller.ClientManager;

public class JoystickControlThread extends ControlThread {
	
	private float _speed;
	private float _direction;
	
	private JoystickView _joystickControlsView;
	
	public JoystickControlThread(JoystickView joystickControlsView) {
		_joystickControlsView = joystickControlsView;
	}
	
	@Override
	protected void reset() {
		_joystickControlsView.reset();
	}
	
	@Override
	public void control() {
		_speed = _joystickControlsView.getSpeed();
		_direction = _joystickControlsView.getDirection();
		
		// note that this is sent continuously
		ClientManager.getInstance().getPlayerClient().getPlayerServer().updateVelocity(_speed, _direction);
	}
	
}
