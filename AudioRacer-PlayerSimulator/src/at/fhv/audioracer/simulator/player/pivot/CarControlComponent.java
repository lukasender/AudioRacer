package at.fhv.audioracer.simulator.player.pivot;

import org.apache.pivot.wtk.Application.UnprocessedKeyHandler;
import org.apache.pivot.wtk.ApplicationContext;
import org.apache.pivot.wtk.Component;
import org.apache.pivot.wtk.Container;
import org.apache.pivot.wtk.Keyboard.KeyCode;
import org.apache.pivot.wtk.Keyboard.KeyLocation;

import at.fhv.audioracer.client.player.PlayerClient;
import at.fhv.audioracer.core.util.Position;

public class CarControlComponent extends Component implements Runnable {
	
	private static final float CONTROL_SENSITY = 1.f;
	protected static final long MAX_CONTROL_WAIT = 10;
	
	private volatile boolean _running;
	protected boolean _speedUp;
	protected boolean _speedDown;
	protected boolean _steerLeft;
	protected boolean _steerRight;
	
	private float _speed;
	private float _direction;
	
	private UnprocessedKeyHandler _keyHandler;
	private PlayerClient _playerClient;
	
	public CarControlComponent() {
		setSkin(new CarControlComponentSkin(this));
		
		_speed = 0.f;
		_direction = 0.f;
		
		_playerClient = PlayerSimulatorWindow.getInstance().getPlayerClient();
		
		_keyHandler = new UnprocessedKeyHandler.Adapter() {
			@Override
			public void keyPressed(int keyCode, KeyLocation keyLocation) {
				switch (keyCode) {
					case KeyCode.W:
						_speedUp = true;
						break;
					case KeyCode.S:
						_speedDown = true;
						break;
					case KeyCode.A:
						_steerLeft = true;
						break;
					case KeyCode.D:
						_steerRight = true;
						break;
				}
			}
			
			@Override
			public void keyReleased(int keyCode, KeyLocation keyLocation) {
				switch (keyCode) {
					case KeyCode.W:
						_speedUp = false;
						break;
					case KeyCode.S:
						_speedDown = false;
						break;
					case KeyCode.A:
						_steerLeft = false;
						break;
					case KeyCode.D:
						_steerRight = false;
						break;
				}
				
			}
		};
	}
	
	public float getSpeed() {
		return _speed;
	}
	
	public float getDirection() {
		return _direction;
	}
	
	public Position getNextCheckPoint() {
		return _playerClient.getNextCheckpoint();
	}
	
	@Override
	protected void setParent(Container parent) {
		super.setParent(parent);
		
		if (parent == null) {
			_running = false;
			PlayerSimulatorApplication.getInstance().setKeyHandler(null);
		} else {
			startControlThread();
			PlayerSimulatorApplication.getInstance().setKeyHandler(_keyHandler);
		}
	}
	
	private void startControlThread() {
		if (_running) {
			return;
		}
		_running = true;
		new Thread(this, "Car control thread").start();
	}
	
	@Override
	public void run() {
		long lastUpdate = System.currentTimeMillis();
		while (_running) {
			long now = System.currentTimeMillis();
			float sensity = (((now - lastUpdate) / 1000.f) * CONTROL_SENSITY);
			if (_speedUp) {
				_speed = Math.min(1.f, (_speed + sensity));
			} else if (_speedDown) {
				_speed = Math.max(-1.f, (_speed - sensity));
			} else if (_speed < 0) {
				_speed = Math.min(0.f, (_speed + sensity));
			} else if (_speed > 0) {
				_speed = Math.max(0.f, (_speed - sensity));
			}
			
			if (_steerLeft) {
				_direction = Math.max(-1.f, (_direction - sensity));
			} else if (_steerRight) {
				_direction = Math.min(1.f, (_direction + sensity));
			} else if (_direction < 0) {
				_direction = Math.min(0.f, (_direction + sensity));
			} else if (_direction > 0) {
				_direction = Math.max(0.f, (_direction - sensity));
			}
			
			// note that this is sent continuously
			_playerClient.getPlayerServer().updateVelocity(_speed, _direction);
			
			ApplicationContext.queueCallback(new Runnable() {
				
				@Override
				public void run() {
					repaint();
				}
			});
			
			long wait = MAX_CONTROL_WAIT - (System.currentTimeMillis() - lastUpdate);
			lastUpdate = now;
			if (wait > 0) {
				try {
					Thread.sleep(wait);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
