package at.fhv.audioracer.server;

import at.fhv.audioracer.server.model.Player;

public class PlayerConnection extends BaseConnection {
	private Player _player;
	
	/**
	 * used for updateVelocity UDP sequence control
	 */
	private int _lastUpdateVelocitySeqNr = -1;
	
	public void setPlayer(Player player) {
		_player = player;
	}
	
	public Player getPlayer() {
		return _player;
	}
	
	/**
	 * A message is valid if the seqNr arriving is higher or lies "_udpSeqNrDelta" delta behind.
	 * 
	 * @param seqNr
	 *            - the current seqNr of the message
	 * @return true on valid message otherwise message is outdated and false is returned
	 */
	public boolean isValidUpdateVelocityMessage(int seqNr) {
		if (_isValidUDPSequenceNr(seqNr, _lastUpdateVelocitySeqNr)) {
			_lastUpdateVelocitySeqNr = seqNr;
			return true;
		}
		return false;
	}
}
