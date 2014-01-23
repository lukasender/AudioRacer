package at.fhv.audioracer.client.player;

import at.fhv.audioracer.communication.player.IPlayerServer;
import at.fhv.audioracer.communication.player.message.CarImageRequestMessage;
import at.fhv.audioracer.communication.player.message.CarImageResponseMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage;
import at.fhv.audioracer.communication.player.message.PlayerMessage.MessageId;
import at.fhv.audioracer.communication.player.message.ReconnectRequestMessage;
import at.fhv.audioracer.communication.player.message.ReconnectRequestResponse;
import at.fhv.audioracer.communication.player.message.SelectCarRequestMessage;
import at.fhv.audioracer.communication.player.message.SelectCarResponseMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameRequestMessage;
import at.fhv.audioracer.communication.player.message.SetPlayerNameResponseMessage;
import at.fhv.audioracer.communication.player.message.UpdateVelocityMessage;
import at.fhv.audioracer.core.model.Player;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

public class PlayerServerClient extends Listener implements IPlayerServer {
	
	private Object _lock;
	
	private Client _client;
	
	private int _playerIdResponse = Player.INVALID_PLAYER_ID;
	private boolean _reconnectSuccess = false;
	private boolean _selectCarResponse;
	private byte[] _carImageResponse = null;
	
	public PlayerServerClient(Client client) {
		_lock = new Object();
		
		if (client == null) {
			throw new IllegalArgumentException("client must not be null!");
		}
		
		_client = client;
	}
	
	@Override
	public int setPlayerName(String playerName) {
		if (_playerIdResponse != Player.INVALID_PLAYER_ID)
			return _playerIdResponse;
		
		SetPlayerNameRequestMessage setNameMsg = new SetPlayerNameRequestMessage();
		setNameMsg.playerName = playerName;
		_client.sendTCP(setNameMsg);
		
		try {
			synchronized (_lock) {
				_lock.wait(); // could be improved
			}
		} catch (InterruptedException e) {
			System.out.println("InterruptException caught in setPlayerName!");
			_playerIdResponse = Player.INVALID_PLAYER_ID;
		}
		
		return _playerIdResponse;
	}
	
	@Override
	public boolean reconnect(int playerId) {
		
		_reconnectSuccess = false;
		ReconnectRequestMessage reconnectReqMsg = new ReconnectRequestMessage();
		reconnectReqMsg.playerId = _playerIdResponse;
		_client.sendTCP(reconnectReqMsg);
		try {
			synchronized (_lock) {
				_lock.wait();
			}
		} catch (InterruptedException e) {
			return false;
		}
		return _reconnectSuccess;
	}
	
	@Override
	public void disconnect() {
		PlayerMessage disconnectMsg = new PlayerMessage(MessageId.DISCONNECT);
		_client.sendTCP(disconnectMsg);
	}
	
	@Override
	public void updateVelocity(float speed, float direction) {
		UpdateVelocityMessage msg = new UpdateVelocityMessage();
		msg.speed = speed;
		msg.direction = direction;
		_client.sendUDP(msg);
	}
	
	@Override
	public boolean selectCar(byte carId) {
		_selectCarResponse = false;
		
		SelectCarRequestMessage msg = new SelectCarRequestMessage();
		msg.carId = carId;
		_client.sendTCP(msg);
		
		synchronized (_lock) {
			try {
				_lock.wait(); // could be improved
			} catch (InterruptedException e) {
				return false;
			}
		}
		
		return _selectCarResponse;
	}
	
	@Override
	public byte[] getCarImage(byte carId) {
		_carImageResponse = null;
		
		CarImageRequestMessage msg = new CarImageRequestMessage();
		msg.carId = carId;
		_client.sendTCP(msg);
		
		synchronized (_lock) {
			try {
				_lock.wait(); // could be improved
			} catch (InterruptedException e) {
				return null;
			}
		}
		
		return _carImageResponse;
	}
	
	@Override
	public void setPlayerReady() {
		PlayerMessage msg = new PlayerMessage(MessageId.SET_READY);
		_client.sendTCP(msg);
	}
	
	public void received(Connection connection, Object object) {
		if (object instanceof PlayerMessage) {
			PlayerMessage message = (PlayerMessage) object;
			switch (message.messageId) {
				case SET_PLAYER_NAME_RESPONSE:
					SetPlayerNameResponseMessage setNameResponse = (SetPlayerNameResponseMessage) message;
					_playerIdResponse = setNameResponse.playerId;
					synchronized (_lock) {
						_lock.notifyAll(); // could be improved
					}
					break;
				case SELECT_CAR_RESPONSE:
					SelectCarResponseMessage selectCarResponseMessage = (SelectCarResponseMessage) message;
					_selectCarResponse = selectCarResponseMessage.successfull;
					synchronized (_lock) {
						_lock.notifyAll(); // could be improved
					}
					break;
				case GET_CAR_IMG_RESPONSE:
					CarImageResponseMessage carImageResponseMessage = (CarImageResponseMessage) message;
					_carImageResponse = carImageResponseMessage.image;
					synchronized (_lock) {
						_lock.notifyAll(); // could be improved
					}
					break;
				case RECONNECT_RESPONSE:
					ReconnectRequestResponse reconnectRespMsg = (ReconnectRequestResponse) message;
					_reconnectSuccess = reconnectRespMsg.reconnectSuccess;
					System.out.println("Reconnect response: " + _reconnectSuccess);
					synchronized (_lock) {
						_lock.notifyAll();
					}
					break;
				default:
					// System.out.println("Message with message id : " + message.messageId
					// + " not known in PlayerServerClient!");
					break;
			}
		}
	}
	
	@Override
	public void trim() {
		PlayerMessage msg = new PlayerMessage(MessageId.TRIM);
		_client.sendTCP(msg);
	}
}
