package at.fhv.audioracer.simulator.world.impl.exception;

public class NoCarsAddedException extends Exception {
	
	private static final long serialVersionUID = -3916858592469935248L;
	
	public NoCarsAddedException() {
		super();
	}
	
	public NoCarsAddedException(String message) {
		super(message);
	}
	
}
