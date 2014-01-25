package at.fhv.audioracer.network.reconnect;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import at.fhv.audioracer.core.model.Player;

public class PlayerTimeoutScheduler {
	private Logger _logger = LoggerFactory.getLogger(PlayerTimeoutScheduler.class);
	private ScheduledExecutorService _scheduledThreadPool = null;
	private HashMap<Integer, ScheduledFuture<?>> _currentTimeoutFutures = new HashMap<>();
	private Set<IPlayerTimeoutEvent> _timeoutEventListenerList = new HashSet<>();
	private int TIMEOUT_IN_SECONDS = 15;
	
	public PlayerTimeoutScheduler(int poolSize) {
		_scheduledThreadPool = Executors.newScheduledThreadPool(poolSize);
	}
	
	public void startTimeout(int playerId) {
		synchronized (_currentTimeoutFutures) {
			// stop current timeout if one is present for that player
			if (_currentTimeoutFutures.containsKey(playerId)) {
				_logger.debug("Try cancel previous timeout for playerId: {}", playerId);
				_currentTimeoutFutures.get(playerId).cancel(true);
			}
			ScheduledFuture<?> future = _scheduledThreadPool.schedule(new Timeout(playerId),
					TIMEOUT_IN_SECONDS, TimeUnit.SECONDS);
			_currentTimeoutFutures.put(playerId, future);
			_logger.debug(
					"Start new timeout for playerId: {} current timeout candidate count: {} ----------------------------- ",
					playerId, _currentTimeoutFutures.size());
		}
	}
	
	public void stopTimeout(int playerId) {
		removeFuture(playerId, true);
	}
	
	public synchronized void registerEvent(IPlayerTimeoutEvent event) {
		_timeoutEventListenerList.add(event);
	}
	
	private class Timeout implements Runnable {
		
		private int _playerId = Player.INVALID_PLAYER_ID;
		
		public Timeout(int playerId) {
			_playerId = playerId;
		}
		
		@Override
		public void run() {
			_logger.debug("Timeout for playerId: {} triggered, notify listeners ...", _playerId);
			
			try {
				notifyListeners(_playerId);
				removeFuture(_playerId, false);
			} catch (Exception e) {
				_logger.error("Exception caught in Timeout-Thread.", e);
			}
		}
	}
	
	public int getPlayersToTimeout() {
		return _currentTimeoutFutures.size();
	}
	
	private void notifyListeners(int playerId) {
		for (IPlayerTimeoutEvent listeners : _timeoutEventListenerList) {
			listeners.playerTimeout(playerId);
		}
		_logger.debug("listeners notified for player-id: {}. Up next: remove Future.", playerId);
	}
	
	private void removeFuture(int playerId, boolean cancelFuture) {
		_logger.debug("remove Future START for player-id: {}", playerId);
		synchronized (_currentTimeoutFutures) {
			if (_currentTimeoutFutures.containsKey(playerId)) {
				if (cancelFuture == true) {
					_currentTimeoutFutures.get(playerId).cancel(cancelFuture);
				}
				_currentTimeoutFutures.remove(playerId);
				_logger.debug(
						"Stop timeout for playerId: {} current timeout candidate count: {} -------------------------------- ",
						playerId, _currentTimeoutFutures.size());
			} else {
				_logger.debug(
						"Stop timeout for playerId: {} ... we have queued Feature ... current timeouut candidate count: {} -------------------",
						playerId, _currentTimeoutFutures.size());
			}
		}
		_logger.debug("remove Future END for player-id: {}", playerId);
	}
}
