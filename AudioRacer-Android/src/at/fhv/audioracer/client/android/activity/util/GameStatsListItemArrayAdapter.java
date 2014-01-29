package at.fhv.audioracer.client.android.activity.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.fhv.audioracer.client.android.R;

public class GameStatsListItemArrayAdapter extends ArrayAdapter<GameStatsEntry> {
	
	private final Context _context;
	private Map<String, GameStatsEntry> _entries;
	private GameStatsEntry[] _values = new GameStatsEntry[0];
	
	public GameStatsListItemArrayAdapter(Context context) {
		super(context, R.layout.layout_game_stats_list_item);
		
		_entries = new HashMap<String, GameStatsEntry>();
		_values = new GameStatsEntry[0];
		_context = context;
	}
	
	@Override
	public void add(GameStatsEntry object) {
		clear();
		_entries.put(object.playerName, object);
		_values = prepareData();
		addAll(_values);
	}
	
	private GameStatsEntry[] prepareData() {
		GameStatsEntry[] sorted = _entries.values().toArray(new GameStatsEntry[_entries.keySet().size()]);
		
		Arrays.sort(sorted, new Comparator<GameStatsEntry>() {
			@Override
			public int compare(GameStatsEntry lhs, GameStatsEntry rhs) {
				int coins = lhs.coinsLeft.compareTo(rhs.coinsLeft);
				if (coins != 0) {
					return coins;
				} else {
					if (rhs.time.equals("-") && !lhs.time.equals("-")) {
						return -1;
					} else if (lhs.time.equals("-") && !rhs.time.equals("-")) {
						return 1;
					}
					return lhs.time.compareTo(rhs.time);
				}
			}
		});
		
		return sorted;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.layout_game_stats_list_item, parent, false);
		TextView firstLine = (TextView) rowView.findViewById(R.id.game_stats_first_line);
		TextView secondLine = (TextView) rowView.findViewById(R.id.game_stats_second_line);
		// ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		firstLine.setText(_values[position].playerName);
		secondLine.setText(_values[position].time + " " + _values[position].coinsLeft);
		
		return rowView;
	}
	
}
