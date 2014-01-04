package at.fhv.audioracer.client.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.activity.listener.IControlMode;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.PlayerReadyAsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;
import at.fhv.audioracer.client.android.util.SystemUiHider;
import at.fhv.audioracer.client.android.util.SystemUiHiderAndroidRacer;

/**
 * This is the main activity to play the game.<br>
 * At first, a player can choose between different 'control modes':<br>
 * <ul>
 * <li>Standard controls: 4 buttons layout: up, down, left, right</li>
 * <li>(not yet implemented) Joystick controls: use an 'on-screen-joystick' to control the car.</li>
 * <li>(not yet implemented) Motion controls: use the motion sensors to control the car.</li>
 * </ul>
 */
public class PlayGameActivity extends Activity implements IControlMode {
	
	private SystemUiHider _systemUiHider;
	
	public static enum ControlMode {
		STANDARD,
		// JOYSTICK,
		// SENSOR,
		;
	}
	
	protected boolean _speedUp;
	protected boolean _speedDown;
	protected boolean _steerLeft;
	protected boolean _steerRight;
	
	private View _chooseControlsView;
	private View _standardControlsView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_play_game);
		
		// get all views
		_chooseControlsView = findViewById(R.id.choose_controls);
		_standardControlsView = findViewById(R.id.standard_controls);
		
		// set other views than 'chooseControlsView' invisible
		_standardControlsView.setVisibility(View.INVISIBLE);
		
		/* ChooseControls */
		
		// Choose 'Standard controls'
		// set 'chooseControlsView' to invisible, set 'standardControlsView' to visible
		final Button stdCtrlButton = (Button) findViewById(R.id.std_ctrl);
		stdCtrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ready(ControlMode.STANDARD);
			}
		});
		
		final Button upButton = (Button) findViewById(R.id.std_ctrl_up);
		final Button downButton = (Button) findViewById(R.id.std_ctrl_down);
		final Button leftButton = (Button) findViewById(R.id.std_ctrl_left);
		final Button rightButton = (Button) findViewById(R.id.std_ctrl_right);
		upButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					_speedUp = true;
				}
				if (MotionEvent.ACTION_UP == event.getAction()) {
					_speedUp = false;
				}
				Log.d("foo", "speedUp " + _speedUp);
				return false;
			}
		});
		downButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					_speedDown = true;
				}
				if (MotionEvent.ACTION_UP == event.getAction()) {
					_speedDown = false;
				}
				Log.d("foo", "speedDown " + _speedDown);
				return false;
			}
		});
		leftButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					_steerLeft = true;
				}
				if (MotionEvent.ACTION_UP == event.getAction()) {
					_steerLeft = false;
				}
				Log.d("foo", "steerLeft " + _steerLeft);
				return false;
			}
		});
		rightButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (MotionEvent.ACTION_DOWN == event.getAction()) {
					_steerRight = true;
				}
				if (MotionEvent.ACTION_UP == event.getAction()) {
					_steerRight = false;
				}
				Log.d("foo", "steerRight " + _steerRight);
				return false;
			}
		});
		
		/* End of: ChooseControls */
		
		// hide SystemUi
		_systemUiHider = new SystemUiHiderAndroidRacer(this, _standardControlsView, SystemUiHider.FLAG_FULLSCREEN);
		_systemUiHider.setup();
		_systemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			@Override
			public void onVisibilityChange(boolean visible) {
				Log.d("foo", "onVisibilityChange: visible? " + visible);
			}
		});
		
		_systemUiHider.hide();
	}
	
	private void ready(ControlMode mode) {
		new PlayerReadyAsyncTask(this, mode).execute(new NetworkParams());
	}
	
	@Override
	public void setControlMode(ControlMode mode) {
		switch (mode) {
			case STANDARD:
				_chooseControlsView.setVisibility(View.INVISIBLE);
				_standardControlsView.setVisibility(View.VISIBLE);
				
				_running = true;
				new StandardControlThread().start();
				
				break;
			default:
				// set control to STANDARD
				setControlMode(ControlMode.STANDARD);
		}
	}
	
	/* copied from AudioRacer-PlayerSimulator: package at.fhv.audioracer.simulator.player.pivot.CarControlComponent */
	
	private static final float CONTROL_SENSITY = 1.f;
	protected static final long MAX_CONTROL_WAIT = 10;
	
	private volatile boolean _running;
	private float _speed;
	private float _direction;
	
	protected class StandardControlThread extends Thread {
		
		@Override
		public void run() {
			long lastUpdate = System.currentTimeMillis();
			while (_running) {
				long now = System.currentTimeMillis();
				float sensity = (((now - lastUpdate) / 1000.f) * CONTROL_SENSITY);
				if (_speedUp) {
					_speed = Math.min(1.f, (_speed + sensity));
				} else if (_speedDown) {
					_speed = Math.max(-1.f, (_speed - sensity));
				} else if (_speed < 0) {
					_speed = Math.min(0.f, (_speed + sensity));
				} else if (_speed > 0) {
					_speed = Math.max(0.f, (_speed - sensity));
				}
				
				if (_steerLeft) {
					_direction = Math.max(-1.f, (_direction - sensity));
				} else if (_steerRight) {
					_direction = Math.min(1.f, (_direction + sensity));
				} else if (_direction < 0) {
					_direction = Math.min(0.f, (_direction + sensity));
				} else if (_direction > 0) {
					_direction = Math.max(0.f, (_direction - sensity));
				}
				
				// note that this is sent continuously
				ClientManager.getInstance().getPlayerClient().getPlayerServer().updateVelocity(_speed, _direction);
				
				long wait = MAX_CONTROL_WAIT - (System.currentTimeMillis() - lastUpdate);
				lastUpdate = now;
				if (wait > 0) {
					try {
						Thread.sleep(wait);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/* end of: copied from AudioRacer-PlayerSimulator: package at.fhv.audioracer.simulator.player.pivot.CarControlComponent */
	
}
