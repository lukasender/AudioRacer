package at.fhv.audioracer.server;

public class CameraConnection extends BaseConnection {
	
	/**
	 * used for updateCar UDP sequence control
	 */
	private int _lastUpdateCarSeqNr = -1;
	
	public boolean isValidUpdateCarMessage(int seqNr) {
		if (_isValidUDPSequenceNr(seqNr, _lastUpdateCarSeqNr)) {
			_lastUpdateCarSeqNr = seqNr;
			return true;
		}
		return false;
	}
}
