package at.fhv.audioracer.client.android.activity.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import at.fhv.audioracer.client.android.R;

public class GameStatsListItemArrayAdapter extends ArrayAdapter<GameStatsEntry> {
	
	private final Context _context;
	private final GameStatsEntry[] _values;
	
	public GameStatsListItemArrayAdapter(Context context, GameStatsEntry[] values) {
		super(context, R.layout.layout_game_stats_list_item, values);
		_context = context;
		_values = values;
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
