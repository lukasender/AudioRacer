package at.fhv.audioracer.server;

import at.fhv.audioracer.server.model.Player;

import com.esotericsoftware.kryonet.Connection;

public class PlayerConnection extends Connection {
	private Player _player;
	
	/**
	 * used for updateVelocity UDP sequence control
	 */
	private int _lastUpdateVelocitySeqNr = -1;
	
	/**
	 * In theory 100 messages arrive in each second. <br/>
	 * We can choose a really high delta because the delta is only used to detect integer overflows.
	 */
	private int _udpSeqNrDelta = 33333;
	
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
		if (seqNr > _lastUpdateVelocitySeqNr || seqNr < (_lastUpdateVelocitySeqNr - _udpSeqNrDelta)) {
			_lastUpdateVelocitySeqNr = seqNr;
			return true;
		}
		return false;
	}
}
