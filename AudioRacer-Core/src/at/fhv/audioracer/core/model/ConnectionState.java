package at.fhv.audioracer.core.model;

import java.awt.Color;

public enum ConnectionState {
	CONNECTED(new Color(102, 255, 102), "Connected"), RECONNECTING(new Color(
			255, 0, 0), "Reconnecting");

	private final Color _c;
	private final String _desc;

	ConnectionState(Color c, String desc) {
		_c = c;
		_desc = desc;
	}

	public Color getColor() {
		return _c;
	}

	public String getDescription() {
		return _desc;
	}
}
