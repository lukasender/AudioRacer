package at.fhv.audioracer.client.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.info.GameInfo;
import at.fhv.audioracer.client.android.network.task.StartClientAsyncTask;
import at.fhv.audioracer.client.android.network.task.params.StartClientParams;
import at.fhv.audioracer.client.android.network.util.AndroidThreadProxy;
import at.fhv.audioracer.client.android.network.util.IThreadProxy;
import at.fhv.audioracer.client.android.util.Defaults;
import at.fhv.audioracer.client.player.IServerDiscoverListener;
import at.fhv.audioracer.client.player.ServerDiscover;

public class JoinGameActivity extends ListActivity implements IServerDiscoverListener, IThreadProxy {
	
	private static final String GAME_NAME = "AudioRacer";
	
	private List<HashMap<String, String>> _games = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter _gamesListAdapter = null;
	
	private ServerDiscover _serverDiscover;
	
	public JoinGameActivity() {
		super();
		_serverDiscover = new ServerDiscover();
		_serverDiscover.getListenerList().add((IServerDiscoverListener) new AndroidThreadProxy(this).getProxy());
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
		_gamesListAdapter = new SimpleAdapter(this, // context
				_games, // data
				android.R.layout.two_line_list_item, // resource
				new String[] { GameInfo.NAME, GameInfo.INFO }, // Array of cursor columns to bind to.
				new int[] { android.R.id.text1, android.R.id.text2 } // Parallel array of which template objects to bind to those columns.
		);
		setListAdapter(_gamesListAdapter);
		
		Button refreshGames = (Button) findViewById(R.id.refresh_games_button);
		refreshGames.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					clearGames();
					_serverDiscover.clearCache();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		final Intent selectCarsIntent = new Intent(this, SelectCarActivity.class);
		ListView gamesListView = (ListView) findViewById(android.R.id.list);
		gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				
				ClientManager manager = ClientManager.getInstance();
				
				// get player name
				CharSequence playerNameCS = manager.retreivePlayerName(JoinGameActivity.this);
				String playerName = Defaults.PLAYER_NAME;
				if (playerNameCS != null) {
					playerName = playerNameCS.toString();
				} else {
					Log.e(ACTIVITY_SERVICE, "Something went wrong? player name is null");
				}
				
				// try to connect and switch to next activity.
				@SuppressWarnings("unchecked")
				HashMap<String, String> game = (HashMap<String, String>) parent.getItemAtPosition(position);
				StartClientAsyncTask startClient = new StartClientAsyncTask();
				StartClientParams params = new StartClientParams();
				params.playerName = playerName;
				params.host = game.get(GameInfo.INFO);
				startClient.execute(params);
				
				_serverDiscover.stopDiscover();
				
				startActivity(selectCarsIntent);
			}
		});
		
		_serverDiscover.start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_game, menu);
		return true;
	}
	
	@Override
	public void onServerDiscovered(String host) {
		addGame(new GameInfo(GAME_NAME, host));
	}
	
	@Override
	public void onServerLost(String host) {
		removeGame(host);
	}
	
	public void addGame(GameInfo game) {
		HashMap<String, String> gameMap = new HashMap<String, String>();
		gameMap.put(GameInfo.NAME, game.getName());
		gameMap.put(GameInfo.INFO, game.getInfo());
		_games.add(gameMap);
		Log.d(ACTIVITY_SERVICE, "Added game with host '" + game.getInfo() + "'");
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	private void removeGame(String host) {
		GameInfo toDelete = new GameInfo(GAME_NAME, host);
		HashMap<String, String> gameMap = new HashMap<String, String>();
		gameMap.put(GameInfo.NAME, toDelete.getName());
		gameMap.put(GameInfo.INFO, toDelete.getInfo());
		boolean removed = _games.remove(gameMap);
		if (removed) {
			Log.d(ACTIVITY_SERVICE, "Removed game with host '" + host + "'");
		}
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	public void clearGames() {
		_games.clear();
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public View getView() {
		return getListView();
	}
	
}
