package at.fhv.audioracer.client.android.activity.thread;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.Display;
import android.widget.ImageView;
import at.fhv.audioracer.client.android.activity.PlayGameActivity;

public class MotionSensorControlThread extends ControlThread {
	
	private Bitmap _bmp;
	private Paint _cPaint;
	private Paint _pPaint;
	private Canvas _cv;
	
	private Integer _minDim = null;
	
	float _x;
	float _y;
	float _z;
	
	private SensorManager _sensorManager;
	private Sensor _sensor;
	
	private SensorEventListener _sensorListener;
	
	private PlayGameActivity _activity;
	private ImageView _msCtrlImgView;
	
	public MotionSensorControlThread(PlayGameActivity activity, ImageView msCtrlImgView) {
		_activity = activity;
		_msCtrlImgView = msCtrlImgView;
		
		int cxy = getMinScreenDimension();
		_bmp = Bitmap.createBitmap(cxy, cxy, Bitmap.Config.ARGB_8888);
		_cv = new Canvas(_bmp);
		
		// circle paint
		_cPaint = new Paint();
		_cPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		_cPaint.setColor(Color.WHITE);
		
		// point paint
		_pPaint = new Paint();
		_pPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
		_pPaint.setColor(Color.BLUE);
	}
	
	private int getMinScreenDimension() {
		if (_minDim == null) {
			Display display = _activity.getWindowManager().getDefaultDisplay();
			Point size = new Point();
			display.getSize(size);
			_minDim = Math.min(size.x, size.y);
			Log.d(Activity.ACTIVITY_SERVICE, "getMinScreenDimension: " + _minDim);
		}
		return _minDim;
	}
	
	private void initSensorValues() {
		if (_sensorManager == null) {
			_sensorManager = (SensorManager) _activity.getSystemService(Context.SENSOR_SERVICE);
			_sensor = _sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
			_sensorListener = new SensorEventListener() {
				
				@Override
				public void onSensorChanged(SensorEvent event) {
					float[] values = event.values;
					_x = values[0];
					_y = values[1];
					_z = values[2];
					String xyz = String.format("%f4.5\t%f4.5\t%f4.5", _x, _y, _z);
					Log.d("sensor", xyz);
				}
				
				@Override
				public void onAccuracyChanged(Sensor sensor, int accuracy) {
					// no op
				}
				
			};
			_sensorManager.registerListener(_sensorListener, _sensor, SensorManager.SENSOR_DELAY_GAME);
		}
	}
	
	@Override
	protected void reset() {
		if (_sensorManager != null) {
			_sensorManager.unregisterListener(_sensorListener);
		}
		_sensorManager = null;
		_sensor = null;
	}
	
	@Override
	public void control() {
		initSensorValues();
		
		final int cxy = getMinScreenDimension();
		final int centerXY = cxy / 2;
		final int radius = cxy / 2;
		final int motionX = centerXY + (int) (radius * _y);
		final int motionY = centerXY + (int) (radius * _x);
		
		_msCtrlImgView.post(new Runnable() {
			@Override
			public void run() {
				
				_cv.drawCircle(centerXY, centerXY, radius, _cPaint);
				_cv.drawCircle(motionX, motionY, 5, _pPaint);
				
				_msCtrlImgView.setBackground(new BitmapDrawable(_activity.getResources(), _bmp));
			}
		});
	}
}