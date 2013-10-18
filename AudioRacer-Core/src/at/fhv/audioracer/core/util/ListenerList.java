package at.fhv.audioracer.core.util;

import java.util.LinkedList;
import java.util.List;

public class ListenerList<T extends IListener> {
	private List<T> _listeners;
	
	public ListenerList() {
		_listeners = new LinkedList<T>();
	}
	
	public void add(T listener) {
		synchronized (_listeners) {
			_listeners.add(listener);
		}
	}
	
	public void remove(T listener) {
		synchronized (_listeners) {
			_listeners.remove(listener);
		}
	}
	
	protected Iterable<T> listeners() {
		List<T> l;
		
		synchronized (_listeners) {
			l = new LinkedList<T>(_listeners);
		}
		
		return l;
	}
}
