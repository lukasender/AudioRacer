package at.fhv.audioracer.client.android.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.util.SystemUiHider;
import at.fhv.audioracer.client.android.util.SystemUiHiderAndroidRacer;
import at.fhv.audioracer.communication.player.IPlayerServer;

/**
 * This is the main activity to play the game.<br>
 * At first, a player can choose between different 'control modes':<br>
 * <ul>
 * <li>Standard controls: 4 buttons layout: up, down, left, right</li>
 * <li>(not yet implemented) Joystick controls: use an 'on-screen-joystick' to control the car.</li>
 * <li>(not yet implemented) Motion controls: use the motion sensors to control the car.</li>
 * </ul>
 */
public class PlayGameActivity extends Activity {
	
	private SystemUiHider _systemUiHider;
	
	private IPlayerServer _playerServer;
	
	protected boolean _speedUp;
	protected boolean _speedDown;
	protected boolean _steerLeft;
	protected boolean _steerRight;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_play_game);
		
		_playerServer = ClientManager.getInstance().getPlayerClient().getPlayerServer();
		
		// get all views
		final View chooseControlsView = findViewById(R.id.choose_controls);
		final View standardControlsView = findViewById(R.id.standard_controls);
		
		// set other views than 'chooseControlsView' invisible
		standardControlsView.setVisibility(View.INVISIBLE);
		
		/* ChooseControls */
		
		// Choose 'Standard controls'
		// set 'chooseControlsView' to invisible, set 'standardControlsView' to visible
		final Button stdCtrlButton = (Button) findViewById(R.id.std_ctrl);
		stdCtrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				chooseControlsView.setVisibility(View.INVISIBLE);
				standardControlsView.setVisibility(View.VISIBLE);
				ready();
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
		_systemUiHider = new SystemUiHiderAndroidRacer(this, standardControlsView, SystemUiHider.FLAG_FULLSCREEN);
		_systemUiHider.setup();
		_systemUiHider.setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
			@Override
			public void onVisibilityChange(boolean visible) {
				Log.d("foo", "onVisibilityChange: visible? " + visible);
			}
		});
		
		_systemUiHider.hide();
	}
	
	private void ready() {
		// TODO
		// _playerServer.setPlayerReady();
	}
	
}
