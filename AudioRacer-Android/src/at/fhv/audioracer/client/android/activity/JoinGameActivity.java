package at.fhv.audioracer.client.android.activity;

import java.net.InetAddress;
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
import at.fhv.audioracer.communication.player.PlayerNetwork;

import com.esotericsoftware.kryonet.Client;

public class JoinGameActivity extends ListActivity {
	
	private List<HashMap<String, String>> _games = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter _gamesListAdapter = null;
	
	private int _id;
	
	private Client _client;
	
	private static final int TIMEOUT = 5000; // ms
	
	private void startClient() {
		_client = new Client();
		_client.start();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_join_game);
		
		startClient();
		
		_gamesListAdapter = new SimpleAdapter(this, // context
				_games, // data
				android.R.layout.two_line_list_item, // resource
				new String[] { Game.NAME, Game.INFO }, // Array of cursor columns to bind to.
				new int[] { android.R.id.text1, android.R.id.text2 } // Parallel array of which template objects to bind to those columns.
		);
		setListAdapter(_gamesListAdapter);
		
		Button refreshGames = (Button) findViewById(R.id.refresh_games_button);
		refreshGames.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InetAddress address = _client.discoverHost(PlayerNetwork.PLAYER_SERVICE_PORT, TIMEOUT);
				addGame(new Game("Game " + _id + ": " + address.getHostName(), "InetAddress: " + address.getHostAddress()));
				_id++;
			}
		});
		
		ListView gamesListView = (ListView) findViewById(android.R.id.list);
		gamesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				@SuppressWarnings("unchecked")
				HashMap<String, String> game = (HashMap<String, String>) parent.getItemAtPosition(position);
				
				Toast.makeText(getApplicationContext(), "Click ListItem Number " + position + "\nGame: " + game.get(Game.NAME), Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.join_game, menu);
		return true;
	}
	
	private class Game {
		public static final String NAME = "name";
		public static final String INFO = "info";
		
		private String _name;
		private String _info;
		
		public Game(String name, String info) {
			_name = name;
			_info = info;
		}
	}
	
	private void addGame(Game game) {
		HashMap<String, String> gameMap = new HashMap<String, String>();
		gameMap.put(Game.NAME, game._name);
		gameMap.put(Game.INFO, game._info);
		_games.add(gameMap);
		_gamesListAdapter.notifyDataSetChanged();
	}
	
}
