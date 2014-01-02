package at.fhv.audioracer.serial;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;
import jssc.SerialPortList;
import at.fhv.audioracer.serial.CarClient.Velocity;
import at.fhv.audioracer.server.CarClientManager;

public class SerialInterface implements SerialPortEventListener, ICarClientListener {
	private static final byte CAR_CONNECTED = 0x1;
	private static final byte CAR_DISCONNECTED = 0x2;
	private static final byte CAR_UPDATE_VELOCITY = 0x3;
	
	private final Lock _lock;
	
	private final SerialPort _serialPort;
	
	private boolean _running;
	
	// used to determine if a log message is sent
	private boolean _logging = false;
	private boolean _carriageReturnReceived = false;
	
	private Map<Byte, CarClient> _carClients;
	private BlockingQueue<CarClient> _writingQueue;
	
	public SerialInterface(String port) throws SerialPortException {
		_lock = new ReentrantLock();
		
		_serialPort = new SerialPort(port);
		_serialPort.openPort();
		
		_serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);
		_serialPort.setRTS(true);
		_serialPort.addEventListener(this, (SerialPort.MASK_CTS | SerialPort.MASK_RXCHAR));
		_serialPort.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8,
				SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, true, false);
		
		_carClients = new HashMap<Byte, CarClient>();
		
		_writingQueue = new LinkedBlockingDeque<CarClient>();
		startWriting();
	}
	
	public static String[] getPortNames() {
		return SerialPortList.getPortNames();
	}
	
	@Override
	public void serialEvent(SerialPortEvent serialPortEvent) {
		try {
			if (serialPortEvent.isRXCHAR()) {
				byte[] buff = _serialPort.readBytes(1);
				if (_logging) {
					if (buff[0] == '\n') {
						_carriageReturnReceived = false;
						_logging = false;
						System.out.println();
					} else {
						if (_carriageReturnReceived) {
							_carriageReturnReceived = false;
							System.out.print('\r');
						}
						if (buff[0] == '\r') {
							_carriageReturnReceived = true;
						} else {
							System.out.print((char) buff[0]);
						}
					}
				} else {
					switch (buff[0]) {
						case 'D':
						case 'W':
						case 'E':
							// Logging
							System.out.print((char) buff[0]);
							_logging = true;
							break;
						case CAR_CONNECTED:
							carConnected();
							break;
						case CAR_DISCONNECTED:
							carDisconnected();
							break;
					}
				}
			} else if (serialPortEvent.isCTS()) {
				startWriting();
			}
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void carConnected() throws SerialPortException {
		byte id = _serialPort.readBytes(1)[0];
		
		CarClient carClient = _carClients.get(id);
		if (carClient == null) {
			carClient = new CarClient(id);
			carClient.getListenerList().add(this);
		}
		
		CarClientManager.getInstance().connect(carClient);
	}
	
	private void carDisconnected() throws SerialPortException {
		byte id = _serialPort.readBytes(1)[0];
		
		CarClient carClient = _carClients.get(id);
		if (carClient != null) {
			CarClientManager.getInstance().disconnect(carClient);
		}
	}
	
	@Override
	public void onVelocityChanged(CarClient carClient) {
		_writingQueue.add(carClient);
		startWriting();
	}
	
	/**
	 * Checks if the writing thread is already running. If the thread is not running, it will be started.
	 */
	private void startWriting() {
		_lock.lock();
		try {
			if (!_running && _serialPort.isCTS()) {
				_running = true;
				Thread thread = new Thread(new Runnable() {
					@Override
					public void run() {
						SerialInterface.this.run();
					}
				}, "SerialInterface");
				thread.setDaemon(true);
				thread.start();
			}
		} catch (SerialPortException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			_lock.unlock();
		}
	}
	
	private void run() {
		// Ensure, that _running has always the correct value!
		// This means, before the thread could end, the lock must be acquired.
		// Also _running must be set before the thread starts.
		_lock.lock();
		boolean locked = true;
		try {
			while (_serialPort.isCTS()) {
				// only send when hardware is ready
				
				_lock.unlock();
				locked = false;
				
				CarClient carClient = _writingQueue.take();
				Velocity velocity = carClient.getVelocity();
				if (velocity != null) {
					byte[] buff = new byte[] { CAR_UPDATE_VELOCITY, carClient.getCarClientId(),
							velocity.speed, velocity.direction };
					_serialPort.writeBytes(buff);
				}
				
				_lock.lock();
				locked = true;
			}
		} catch (SerialPortException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (!locked) {
				_lock.lock();
			}
			
			_running = false;
			_lock.unlock();
		}
	}
}
