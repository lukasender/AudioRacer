package at.fhv.audioracer.client.android.info;

public class GameInfo {
	public static final String NAME = "name";
	public static final String INFO = "info";
	
	private HostInfo _host;
	
	private String _name;
	private String _info;
	
	public GameInfo(String name, String info) {
		_name = name;
		_info = info;
		_host = null;
	}
	
	public GameInfo(String name, String info, HostInfo host) {
		this(name, info);
		_host = host;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getInfo() {
		return _info;
	}
	
	public void setInfo(String info) {
		_info = info;
	}
	
	public HostInfo getHostInfo() {
		return _host;
	}
	
	public void setHostInfo(HostInfo host) {
		_host = host;
	}
	
}
