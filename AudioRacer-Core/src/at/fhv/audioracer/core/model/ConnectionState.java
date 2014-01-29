package at.fhv.audioracer.core.model;

public enum ConnectionState {
	CONNECTED(255 << 8, "Connected"), RECONNECTING(255 << 16, "Reconnecting");

	private final int _rgbColorValue;
	private final String _desc;

	ConnectionState(int rgbColorValue, String desc) {
		_rgbColorValue = rgbColorValue;
		_desc = desc;
	}

	public int getRGBColorValue() {
		return _rgbColorValue;
	}

	public String getDescription() {
		return _desc;
	}
}
