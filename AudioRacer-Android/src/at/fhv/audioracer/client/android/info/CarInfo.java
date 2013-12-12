package at.fhv.audioracer.client.android.info;

public class CarInfo {
	
	public static final String NAME = "name";
	public static final String ID = "id";
	
	private String _name;
	private String _id;
	
	public CarInfo(String name, String id) {
		_name = name;
		_id = id;
	}
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String getId() {
		return _id;
	}
	
	public void setId(String id) {
		_id = id;
	}
	
}
