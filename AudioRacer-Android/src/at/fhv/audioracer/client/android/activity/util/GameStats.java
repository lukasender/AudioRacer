package at.fhv.audioracer.client.android.activity.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class GameStats {
	
	private Map<String, GameStatsEntry> _entries;
	
	public GameStats() {
		_entries = new HashMap<String, GameStatsEntry>();
	}
	
	public void addGameStats(GameStatsEntry entry) {
		_entries.put(entry.playerName, entry);
	}
	
	public GameStatsEntry[] getEntriesSorted() {
		GameStatsEntry[] sorted = _entries.values().toArray(new GameStatsEntry[_entries.keySet().size()]);
		
		Arrays.sort(sorted, new Comparator<GameStatsEntry>() {
			@Override
			public int compare(GameStatsEntry lhs, GameStatsEntry rhs) {
				int t = lhs.time.compareTo(rhs.time);
				if (t != 0) {
					return t;
				}
				
				return lhs.coinsLeft.compareTo(rhs.coinsLeft);
			}
		});
		
		return sorted;
	}
	
}
