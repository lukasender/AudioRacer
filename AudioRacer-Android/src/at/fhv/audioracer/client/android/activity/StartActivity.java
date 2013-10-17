package at.fhv.audioracer.client.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.util.Defaults;
import at.fhv.audioracer.client.android.util.Preferences;

public class StartActivity extends Activity {
	
	private CharSequence _playerName = null;
	private EditText _playerNameText = null;
	
	/**
	 * Clear all preferences for {@link Preferences#PLAYER_PREFERENCES}
	 */
	@SuppressWarnings("unused")
	private void clearSharedPreferences() {
		SharedPreferences.Editor editor = getSharedPreferences(Preferences.PLAYER_PREFERENCES, MODE_PRIVATE).edit();
		editor.clear();
		editor.commit();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_start);
		
		/* ***************************** *
		 * only do this while debugging!
		 * ***************************** */
//		clearSharedPreferences();
		
		_playerNameText = (EditText) findViewById(R.id.player_name);
		final Intent gamesIntent = new Intent(this, JoinGameActivity.class);

		// load and set player name
		restorePlayerName();
		
		Button searchGamesButton = (Button) findViewById(R.id.search_games_button);
		searchGamesButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				choosePlayerName();
				startActivity(gamesIntent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.start, menu);
		return true;
	}
	
	private void choosePlayerName() {
		EditText playerName = (EditText) findViewById(R.id.player_name);
		
		// choose current playerName and store it.
		// if the player did not choose a name, a default name is be used.
		if (playerName.getText().length() > 0) {
			_playerName = playerName.getText();
			savePlayerName();
		} else {
			_playerName = Defaults.PLAYER_NAME;
		}
	}
	
	private void restorePlayerName() {
		SharedPreferences pref = getSharedPreferences(Preferences.PLAYER_PREFERENCES, MODE_PRIVATE);
		_playerName = pref.getString(Preferences.PLAYER_NAME, null); // null as default
		// if _playerName is null, the 'hint' message will be shown.
		_playerNameText.setText(_playerName);
		
		Log.d(Preferences.PLAYER_NAME, "Restored playername: " + ((_playerName == null) ? "null" : _playerName.toString()));
	}
	
	private void savePlayerName() {
		SharedPreferences pref = getSharedPreferences(Preferences.PLAYER_PREFERENCES, MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(Preferences.PLAYER_NAME, _playerName.toString());
		
		if (editor.commit()) {
			Log.d(Preferences.PLAYER_NAME, "new player name saved: " + _playerName);
		} else {
			Log.e(Preferences.PLAYER_NAME, "could not save new player name");
		}
	}
	
}
