package at.fhv.audioracer.client.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.info.GameInfo;
import at.fhv.audioracer.client.android.network.util.AndroidThreadProxy;
import at.fhv.audioracer.client.android.network.util.IThreadProxy;
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
					_serverDiscover.stopDiscover();
					clearGames();
					_serverDiscover.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		ListView gamesListView = (ListView) findViewById(android.R.id.list);
		gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> game = (HashMap<String, String>) parent.getItemAtPosition(position);
				
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position + "\nGame: " + game.get(GameInfo.NAME), Toast.LENGTH_SHORT).show();
			}
		});
		
		_serverDiscover.start();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_game, menu);
		return true;
	}
	
	public void addGame(GameInfo game) {
		HashMap<String, String> gameMap = new HashMap<String, String>();
		gameMap.put(GameInfo.NAME, game.getName());
		gameMap.put(GameInfo.INFO, game.getInfo());
		_games.add(gameMap);
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	private void removeGame(String host) {
		GameInfo toDelete = new GameInfo(GAME_NAME, host);
		HashMap<String, String> gameMap = new HashMap<String, String>();
		gameMap.put(GameInfo.NAME, toDelete.getName());
		gameMap.put(GameInfo.INFO, toDelete.getInfo());
		boolean removed = _games.remove(gameMap);
		if (removed) {
			System.out.println("Removed game with host '" + host + "'");
		}
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	public void clearGames() {
		_games.clear();
		_gamesListAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onServerDiscovered(String host) {
		addGame(new GameInfo(GAME_NAME, host));
	}
	
	@Override
	public void onServerLost(String host) {
		removeGame(host);
	}
	
	@Override
	public View getView() {
		return getListView();
	}
	
}
