package at.fhv.audioracer.client.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.SimpleAdapter;
import at.fhv.audioracer.client.android.R;

public class SelectCarActivity extends ListActivity {
	
	/**
	 * Available cars.
	 */
	private List<HashMap<String, String>> _cars = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter _carsListAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_car);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_car, menu);
		return true;
	}
	
}
