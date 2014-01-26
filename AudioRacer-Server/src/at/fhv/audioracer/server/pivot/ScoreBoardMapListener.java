package at.fhv.audioracer.server.pivot;

import java.util.Collection;

import org.apache.pivot.collections.HashMap;
import org.apache.pivot.collections.List;
import org.apache.pivot.collections.Map;

import at.fhv.audioracer.core.model.Car;
import at.fhv.audioracer.core.model.IMapListener;
import at.fhv.audioracer.core.model.Player;

public class ScoreBoardMapListener implements IMapListener {
	
	private at.fhv.audioracer.core.model.Map _map;
	private List<Map<String, String>> _list;
	
	public ScoreBoardMapListener(at.fhv.audioracer.core.model.Map map,
			List<Map<String, String>> list) {
		_map = map;
		_list = list;
	}
	
	@Override
	public void onMapSizeChanged() {
		// no-op.
	}
	
	@Override
	public void onCarAdded(Car<?> addedCar) {
		// no-op.
	}
	
	@Override
	public void onCarRemoved(Car<?> removedCar) {
		// no-op.
	}
	
	@Override
	public void onCheckpointChange() {
		_list.clear();
		
		Collection<Car<?>> cars = _map.getCars();
		for (Car<?> car : cars) {
			Object p = car.getPlayer();
			if (!(p instanceof Player)) {
				continue;
			}
			Player player = (Player) p;
			
			Map<String, String> m = new HashMap<>();
			m.put("player", player.getName());
			m.put("checkpoints", Integer.toString(player.getCoinsLeft()));
			_list.add(m);
		}
	}
	
}
