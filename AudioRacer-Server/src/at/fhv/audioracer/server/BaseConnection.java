package at.fhv.audioracer.server;

import com.esotericsoftware.kryonet.Connection;

public class BaseConnection extends Connection {
	
	/**
	 * In theory 100 messages arrive in each second. <br/>
	 * We can choose a really high delta because the delta is only used to detect integer overflows.
	 */
	private int _udpSeqNrDelta = 3000;
	
	/**
	 * A message is valid if the seqNr arriving is higher or lies "_udpSeqNrDelta" delta behind.
	 * 
	 * @param seqNr
	 *            the current seqNr of the message
	 * @param lastValidSeqNr
	 *            last valid seqNr arrived for that kind of message
	 * @return true on valid message otherwise message is out-dated and false is returned
	 */
	protected boolean _isValidUDPSequenceNr(int seqNr, int lastValidSeqNr) {
		if (seqNr > lastValidSeqNr || seqNr < (lastValidSeqNr - _udpSeqNrDelta)) {
			return true;
		}
		return false;
	}
}
