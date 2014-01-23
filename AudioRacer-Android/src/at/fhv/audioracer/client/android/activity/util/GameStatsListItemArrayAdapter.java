package at.fhv.audioracer.client.android.activity.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import at.fhv.audioracer.client.android.R;

public class GameStatsListItemArrayAdapter extends ArrayAdapter<String> {
	
	private final Context _context;
	private final String[] _values;
	
	public GameStatsListItemArrayAdapter(Context context, String[] values) {
		super(context, R.layout.layout_game_stats_list_item, values);
		_context = context;
		_values = values;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) _context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.layout_game_stats_list_item, parent, false);
		// TextView textView = (TextView) rowView.findViewById(R.id.label);
		// ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
		// textView.setText(values[position]);
		// // Change the icon for Windows and iPhone
		// String s = values[position];
		// if (s.startsWith("iPhone")) {
		// imageView.setImageResource(R.drawable.no);
		// } else {
		// imageView.setImageResource(R.drawable.ok);
		// }
		
		return rowView;
	}
	
}
