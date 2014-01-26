package at.fhv.audioracer.client.android.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.activity.listener.IControlMode;
import at.fhv.audioracer.client.android.activity.thread.JoystickControlThread;
import at.fhv.audioracer.client.android.activity.thread.MotionSensorControlThread;
import at.fhv.audioracer.client.android.activity.thread.StandardControlThread;
import at.fhv.audioracer.client.android.activity.thread.TrimSettingsControlThread;
import at.fhv.audioracer.client.android.activity.thread.util.ThreadControlMode;
import at.fhv.audioracer.client.android.activity.util.GameStats;
import at.fhv.audioracer.client.android.activity.util.GameStatsEntry;
import at.fhv.audioracer.client.android.activity.util.GameStatsListItemArrayAdapter;
import at.fhv.audioracer.client.android.activity.util.PressedButton;
import at.fhv.audioracer.client.android.activity.util.PressedTouchListener;
import at.fhv.audioracer.client.android.activity.view.JoystickView;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.network.task.PlayerReadyAsyncTask;
import at.fhv.audioracer.client.android.network.task.TrimSettingsAsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;
import at.fhv.audioracer.client.android.util.SystemUiHider;
import at.fhv.audioracer.client.android.util.SystemUiHiderAndroidRacer;
import at.fhv.audioracer.client.player.IPlayerClientListener;
import at.fhv.audioracer.core.model.Player;

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
	
	public class OnGameEndsListener extends IPlayerClientListener.Adapter {
		
		private final Intent selectCarsIntent = new Intent(PlayGameActivity.this, SelectCarActivity.class);
		
		@Override
		public void onGameEnd() {
			Toast.makeText(PlayGameActivity.this, "The game has ended and will restart again. Choose a free car!", Toast.LENGTH_SHORT);
			startActivity(selectCarsIntent);
		}
	}
	
	private SystemUiHider _systemUiHider;
	
	public static enum ControlMode {
		STANDARD, SENSOR, SETTINGS_TRIM, JOYSTICK, ;
	}
	
	protected PressedButton _speedUp = new PressedButton();
	protected PressedButton _speedDown = new PressedButton();
	protected PressedButton _steerLeft = new PressedButton();
	protected PressedButton _steerRight = new PressedButton();
	
	private View _controlsSettingsControlsView;
	private View _standardControlsView;
	private View _motionSensorControlsView;
	private JoystickView _joystickControlsView;
	private View _trimSettingsView;
	
	private ListView _gameStatsListView;
	
	private ImageView _msCtrlImgView;
	
	private List<ThreadControlMode> _threads;
	
	private List<View> _views;
	
	private ControlMode _chosenControlMode;
	
	private TextView _tsSpeedValueTextView;
	private TextView _tsSteeringValueTextView;
	private float _trimSpeed;
	private float _trimSteering;
	private PressedButton _trimSpeedUp = new PressedButton();
	private PressedButton _trimSpeedDown = new PressedButton();
	private PressedButton _trimSteeringUp = new PressedButton();
	private PressedButton _trimSteeringDown = new PressedButton();
	
	@Override
	public void onBackPressed() {
		if (_controlsSettingsControlsView.getVisibility() == View.VISIBLE) {
			super.onBackPressed();
		} else {
			stopAllThreads();
			hideAllViews();
			_controlsSettingsControlsView.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.play_game, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_game_stats_list:
				toggleGameStatsListView();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_play_game);
		
		/* get all views */
		// controls
		_controlsSettingsControlsView = findViewById(R.id.controls_settings);
		_standardControlsView = findViewById(R.id.standard_controls);
		_motionSensorControlsView = findViewById(R.id.motion_sensor_controls);
		_joystickControlsView = (JoystickView) findViewById(R.id.joystick_controls);
		// settings
		_trimSettingsView = findViewById(R.id.trim_settings_view);
		// game stats
		_gameStatsListView = (ListView) findViewById(R.id.game_stats_list_view);
		
		final GameStats gameStats = new GameStats();
		final GameStatsListItemArrayAdapter adapter = new GameStatsListItemArrayAdapter(this, gameStats.getEntriesSorted());
		
		IPlayerClientListener listener = new IPlayerClientListener.Adapter() {
			public void onUpdateGameState(Player player) {
				Log.d("play game", "called onUpdateGameState: " + player.getName() + ", coinsLeft " + player.getCoinsLeft() + ", time " + player.getTime());
				GameStatsEntry entry = new GameStatsEntry();
				entry.playerName = player.getName();
				entry.coinsLeft = "" + player.getCoinsLeft();
				Date d = new Date(new Integer(player.getTime()).longValue());
				SimpleDateFormat format = new SimpleDateFormat("mm:ss");
				entry.time = format.format(d);
				
				gameStats.addGameStats(entry);
				
				_gameStatsListView.post(new Runnable() {
					@Override
					public void run() {
						adapter.notifyDataSetChanged();
					}
				});
			};
		};
		
		ClientManager.getInstance().getPlayerClient().getListenerList().add(listener);
		ClientManager.getInstance().getPlayerClient().getListenerList().add(new OnGameEndsListener());
		
		_views = new LinkedList<View>();
		_views.add(_controlsSettingsControlsView);
		_views.add(_standardControlsView);
		_views.add(_motionSensorControlsView);
		_views.add(_joystickControlsView);
		_views.add(_trimSettingsView);
		_views.add(_gameStatsListView);
		
		// get the 'motion sensor' image view.
		// a circle and the current 'motion position' will be drawn onto this view.
		_msCtrlImgView = (ImageView) findViewById(R.id.ms_ctrl_img_view);
		
		// set other views than 'controlsSettingsControlsView' invisible
		_standardControlsView.setVisibility(View.INVISIBLE);
		_motionSensorControlsView.setVisibility(View.INVISIBLE);
		_joystickControlsView.setVisibility(View.INVISIBLE);
		_trimSettingsView.setVisibility(View.INVISIBLE);
		_gameStatsListView.setVisibility(View.INVISIBLE);
		
		/* Settings */
		final Button trimSettingsButton = (Button) findViewById(R.id.trim_settings);
		trimSettingsButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Log.d("trim button", "clicked on trim button");
				_trimSettingsView.setVisibility(View.VISIBLE);
				
				_standardControlsView.setVisibility(View.INVISIBLE);
				_motionSensorControlsView.setVisibility(View.INVISIBLE);
				_joystickControlsView.setVisibility(View.INVISIBLE);
				_controlsSettingsControlsView.setVisibility(View.INVISIBLE);
				
				startThread(ControlMode.SETTINGS_TRIM);
			}
		});
		/* End of: Settings */
		
		final Button tsSpeedUpButton = (Button) findViewById(R.id.trim_settings_speed_up);
		final Button tsSpeedDownButton = (Button) findViewById(R.id.trim_settings_speed_down);
		final Button tsSteeringUpButton = (Button) findViewById(R.id.trim_settings_steering_up);
		final Button tsSteeringDownButton = (Button) findViewById(R.id.trim_settings_steering_down);
		tsSpeedUpButton.setOnTouchListener(new PressedTouchListener(_trimSpeedUp));
		tsSpeedDownButton.setOnTouchListener(new PressedTouchListener(_trimSpeedDown));
		tsSteeringUpButton.setOnTouchListener(new PressedTouchListener(_trimSteeringUp));
		tsSteeringDownButton.setOnTouchListener(new PressedTouchListener(_trimSteeringDown));
		_tsSpeedValueTextView = (TextView) findViewById(R.id.trim_settings_speed_value);
		_tsSteeringValueTextView = (TextView) findViewById(R.id.trim_settings_steering_value);
		
		final Button tsSetTrimButton = (Button) findViewById(R.id.trim_settings_set_trim);
		tsSetTrimButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setTrim();
			}
		});
		
		// possible threads
		_threads = new LinkedList<ThreadControlMode>();
		_threads.add(new ThreadControlMode(ControlMode.STANDARD, new StandardControlThread(_speedUp, _speedDown, _steerLeft, _steerRight)));
		_threads.add(new ThreadControlMode(ControlMode.SENSOR, new MotionSensorControlThread(this, _msCtrlImgView)));
		_threads.add(new ThreadControlMode(ControlMode.SETTINGS_TRIM, new TrimSettingsControlThread(this, _trimSpeedUp, _trimSpeedDown, _trimSteeringUp,
				_trimSteeringDown)));
		_threads.add(new ThreadControlMode(ControlMode.JOYSTICK, new JoystickControlThread(_joystickControlsView)));
		/* ChooseControls */
		
		// Choose 'Standard controls'
		final Button stdCtrlButton = (Button) findViewById(R.id.std_ctrl);
		stdCtrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ready(ControlMode.STANDARD);
			}
		});
		// Choose 'Motion Sensor'
		final Button msCtrlButton = (Button) findViewById(R.id.ms_ctrl);
		msCtrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ready(ControlMode.SENSOR);
			}
		});
		// Choose 'Joystick controls'
		final Button joystickCtrlButton = (Button) findViewById(R.id.joystick_ctrl);
		joystickCtrlButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				ready(ControlMode.JOYSTICK);
			}
		});
		
		final Button upButton = (Button) findViewById(R.id.std_ctrl_up);
		final Button downButton = (Button) findViewById(R.id.std_ctrl_down);
		final Button leftButton = (Button) findViewById(R.id.std_ctrl_left);
		final Button rightButton = (Button) findViewById(R.id.std_ctrl_right);
		upButton.setOnTouchListener(new PressedTouchListener(_speedUp));
		downButton.setOnTouchListener(new PressedTouchListener(_speedDown));
		leftButton.setOnTouchListener(new PressedTouchListener(_steerLeft));
		rightButton.setOnTouchListener(new PressedTouchListener(_steerRight));
		
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
	
	private void hideAllViews() {
		for (View view : _views) {
			view.setVisibility(View.INVISIBLE);
		}
	}
	
	public void trimSpeedUp(float trimStep) {
		_trimSpeed = Math.min(1.0f, _trimSpeed + trimStep);
		setTrimValue(_tsSpeedValueTextView, _trimSpeed);
	}
	
	public void trimSpeedDown(float trimStep) {
		_trimSpeed = Math.max(-1.0f, _trimSpeed - trimStep);
		setTrimValue(_tsSpeedValueTextView, _trimSpeed);
	}
	
	public void trimSteeringUp(float trimStep) {
		_trimSteering = Math.min(1.0f, _trimSteering + trimStep);
		setTrimValue(_tsSteeringValueTextView, _trimSteering);
	}
	
	public void trimSteeringDown(float trimStep) {
		_trimSteering = Math.max(-1.0f, _trimSteering - trimStep);
		setTrimValue(_tsSteeringValueTextView, _trimSteering);
	}
	
	private void setTrim() {
		_trimSpeed = 0.0f;
		_trimSteering = 0.0f;
		setTrimValue(_tsSpeedValueTextView, _trimSpeed);
		setTrimValue(_tsSteeringValueTextView, _trimSteering);
		sendTrim();
	}
	
	/**
	 * Sets given float <code>value</code> to given <code>TextView</code>. It also takes care of the algebraic sign. If the the value is below 0, the value will
	 * be preceded by a '-'; a '+' otherwise.
	 * 
	 * @param view
	 * @param value
	 */
	private void setTrimValue(final TextView view, float value) {
		final char[] v = String.format(" %s%.2f", ((value >= 0) ? "+" : ""), value).toCharArray();
		view.post(new Runnable() {
			@Override
			public void run() {
				view.setText(v, 0, v.length);
			}
		});
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		resume();
	}
	
	private void resume() {
		// enable controls again.
		if (_chosenControlMode != null) {
			setControlMode(_chosenControlMode);
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		stopAllThreads();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopAllThreads();
	}
	
	private void ready(ControlMode mode) {
		new PlayerReadyAsyncTask(this, mode).execute(new NetworkParams());
	}
	
	private void sendTrim() {
		new TrimSettingsAsyncTask().execute(new NetworkParams());
	}
	
	@Override
	public void setControlMode(ControlMode mode) {
		_chosenControlMode = mode;
		switch (mode) {
			case STANDARD:
				_controlsSettingsControlsView.setVisibility(View.INVISIBLE);
				_standardControlsView.setVisibility(View.VISIBLE);
				_motionSensorControlsView.setVisibility(View.INVISIBLE);
				_joystickControlsView.setVisibility(View.INVISIBLE);
				
				startThread(mode);
				break;
			case SENSOR:
				_controlsSettingsControlsView.setVisibility(View.INVISIBLE);
				_standardControlsView.setVisibility(View.INVISIBLE);
				_motionSensorControlsView.setVisibility(View.VISIBLE);
				_joystickControlsView.setVisibility(View.INVISIBLE);
				
				startThread(mode);
				break;
			case JOYSTICK:
				_controlsSettingsControlsView.setVisibility(View.INVISIBLE);
				_standardControlsView.setVisibility(View.INVISIBLE);
				_motionSensorControlsView.setVisibility(View.INVISIBLE);
				_joystickControlsView.setVisibility(View.VISIBLE);
				
				startThread(mode);
				break;
			default:
				// set control to STANDARD
				setControlMode(ControlMode.STANDARD);
		}
	}
	
	private boolean areGameStatsVisible() {
		if (_gameStatsListView.getVisibility() == View.VISIBLE) {
			return true;
		}
		return false;
	}
	
	private void toggleGameStatsListView() {
		if (!areGameStatsVisible()) {
			hideAllViews();
			stopAllThreads();
			_gameStatsListView.setVisibility(View.VISIBLE);
		} else {
			resume();
			_gameStatsListView.setVisibility(View.INVISIBLE);
		}
	}
	
	private void startThread(ControlMode mode) {
		for (ThreadControlMode tcm : _threads) {
			if (tcm.mode == mode) {
				tcm.thread.start();
				return;
			}
		}
	}
	
	private void stopAllThreads() {
		for (ThreadControlMode tcm : _threads) {
			tcm.thread.stop();
		}
	}
	
}
