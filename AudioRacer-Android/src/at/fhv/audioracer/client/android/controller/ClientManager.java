package at.fhv.audioracer.client.android.controller;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import at.fhv.audioracer.client.android.util.Preferences;
import at.fhv.audioracer.client.player.PlayerClient;

public class ClientManager {
	
	private static ClientManager _manager;
	
	private PlayerClient _playerClient;
	
	private ClientManager() {
		_playerClient = new PlayerClient();
	}
	
	public static ClientManager getInstance() {
		if (_manager == null) {
			_manager = new ClientManager();
		}
		
		return _manager;
	}
	
	public PlayerClient getPlayerClient() {
		return _playerClient;
	}
	
	public CharSequence retreivePlayerName(Activity activity) {
		SharedPreferences pref = activity.getSharedPreferences(Preferences.PLAYER_PREFERENCES, Context.MODE_PRIVATE);
		return pref.getString(Preferences.PLAYER_NAME, null); // null as default
	}
	
	public void savePlayerName(Activity activity, CharSequence name) {
		SharedPreferences pref = activity.getSharedPreferences(Preferences.PLAYER_PREFERENCES, Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(Preferences.PLAYER_NAME, name.toString());
		
		if (editor.commit()) {
			Log.d(Preferences.PLAYER_NAME, "new player name saved: " + name);
		} else {
			Log.e(Preferences.PLAYER_NAME, "could not save new player name");
		}
	}
	
}
