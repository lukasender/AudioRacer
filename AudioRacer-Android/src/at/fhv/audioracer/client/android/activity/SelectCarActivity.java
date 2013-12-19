package at.fhv.audioracer.client.android.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import at.fhv.audioracer.client.android.R;
import at.fhv.audioracer.client.android.activity.listener.IFreeCarsListener;
import at.fhv.audioracer.client.android.controller.ClientManager;
import at.fhv.audioracer.client.android.info.CarInfo;
import at.fhv.audioracer.client.android.network.task.FreeCarsAsyncTask;
import at.fhv.audioracer.client.android.network.task.params.NetworkParams;
import at.fhv.audioracer.client.player.IPlayerClientListener;

public class SelectCarActivity extends ListActivity implements IFreeCarsListener {
	
	/**
	 * Available cars.
	 */
	private List<HashMap<String, String>> _cars = new ArrayList<HashMap<String, String>>();
	private SimpleAdapter _carsListAdapter = null;
	
	private IPlayerClientListener _playerClientListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_select_car);
		
		_carsListAdapter = new SimpleAdapter(this, // context
				_cars, // data
				android.R.layout.two_line_list_item, // resource
				new String[] { CarInfo.NAME, CarInfo.ID }, // Array of cursor columns to bind to.
				new int[] { android.R.id.text1, android.R.id.text2 } // Parallel array of which template objects to bind to those columns.
		);
		setListAdapter(_carsListAdapter);
		
		final FreeCarsAsyncTask task = new FreeCarsAsyncTask(SelectCarActivity.this);
		_playerClientListener = new IPlayerClientListener.Adapter() {
			@Override
			public void onUpdateFreeCars() {
				task.execute(new NetworkParams());
			}
		};
		ClientManager.getInstance().getPlayerClient().getListenerList().add(_playerClientListener);
		
		ListView carsListView = (ListView) findViewById(android.R.id.list);
		carsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.select_car, menu);
		return true;
	}
	
	@Override
	public void addFreeCars(int[] freeCarIds) {
		Log.d(ACTIVITY_SERVICE, "called addCars()");
		_cars.clear();
		for (int id : freeCarIds) {
			HashMap<String, String> carMap = new HashMap<String, String>();
			carMap.put(CarInfo.NAME, "Car " + id);
			_cars.add(carMap);
			_carsListAdapter.notifyDataSetChanged();
			Log.d(ACTIVITY_SERVICE, "Added car with id '" + id + "'");
		}
	}
	
}
