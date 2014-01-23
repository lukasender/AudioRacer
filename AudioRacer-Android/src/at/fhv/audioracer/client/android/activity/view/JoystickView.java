package at.fhv.audioracer.client.android.activity.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class JoystickView extends View {
	// Values in pixels for a display with 320x240 and 160dpi
	private static final int CONTROL_THIKNESS = 100;
	private static final int OFFSITE_X = 50;
	private static final int OFFSITE_Y = 50;
	
	private float _controlSize;
	private float _speed;
	private float _direction;
	
	public JoystickView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		_speed = 0f;
		_direction = 0f;
	}
	
	public float getSpeed() {
		return _speed;
	}
	
	public float getDirection() {
		return _direction;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		
		int height = getHeight();
		float density = getResources().getDisplayMetrics().density;
		_controlSize = (height - (((OFFSITE_Y * 2f) + CONTROL_THIKNESS) * density));
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		
		int width = getWidth();
		int height = getHeight();
		
		Paint paint = new Paint();
		paint.setStyle(Paint.Style.STROKE);
		paint.setColor(0xFF000000);
		
		float density = getResources().getDisplayMetrics().density;
		float speedLeft = OFFSITE_X * density;
		float speedTop = OFFSITE_Y * density;
		float speedRight = (OFFSITE_X + CONTROL_THIKNESS) * density;
		float speedBottom = height - speedTop;
		float controlThikness = (speedRight - speedLeft);
		
		float currentTop = speedTop;
		float currentBottom = (speedTop + controlThikness);
		RectF oval = new RectF(speedLeft, currentTop, speedRight, currentBottom);
		float startAngle = 0;
		float sweepAngle = -180;
		canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
		
		currentTop += (currentBottom - currentTop) / 2;
		currentBottom = (height - currentTop);
		canvas.drawLine(speedLeft, currentTop, speedLeft, currentBottom, paint);
		canvas.drawLine(speedRight, currentTop, speedRight, currentBottom, paint);
		
		currentTop = speedBottom - controlThikness;
		currentBottom = speedBottom;
		startAngle = 0;
		sweepAngle = 180;
		oval = new RectF(speedLeft, currentTop, speedRight, currentBottom);
		canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
		
		// direction
		float directionTop = (height / 2) - (controlThikness / 2f);
		float directionRight = width - (OFFSITE_Y * density);
		float directionBottom = directionTop + controlThikness;
		float directionLeft = directionRight - (speedBottom - speedTop);
		
		float currentLeft = directionLeft;
		float currentRight = (directionLeft + controlThikness);
		oval = new RectF(currentLeft, directionTop, currentRight, directionBottom);
		startAngle = 90;
		sweepAngle = 180;
		canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
		
		currentLeft += (currentRight - currentLeft) / 2;
		currentRight = (directionRight - (controlThikness / 2));
		canvas.drawLine(currentLeft, directionTop, currentRight, directionTop, paint);
		canvas.drawLine(currentLeft, directionBottom, currentRight, directionBottom, paint);
		
		currentLeft = directionRight - controlThikness;
		currentRight = directionRight;
		startAngle = 90;
		sweepAngle = -180;
		oval = new RectF(currentLeft, directionTop, currentRight, directionBottom);
		canvas.drawArc(oval, startAngle, sweepAngle, false, paint);
		
		paint.setColor(0x99000000);
		paint.setStyle(Style.FILL);
		float centerX = speedLeft + (controlThikness / 2);
		float centerY = ((height / 2) + (_controlSize * _speed * -1f));
		canvas.drawCircle(centerX, centerY, (controlThikness * 0.55f), paint);
		
		centerX = directionLeft + (controlThikness / 2) + (_controlSize / 2) + (_controlSize * _direction);
		centerY = (height / 2);
		canvas.drawCircle(centerX, centerY, (controlThikness * 0.55f), paint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_MOVE:
				boolean speedUpdated = false;
				boolean directionUpdated = false;
				for (int i = 0; i < event.getPointerCount(); i++) {
					float x = event.getX(i);
					if (isInSpeedArea(x)) {
						updateSpeed(event.getY(i));
						speedUpdated = true;
					} else {
						updateDirection(x);
						directionUpdated = true;
					}
				}
				
				if (!speedUpdated) {
					_speed = 0f;
				}
				if (!directionUpdated) {
					_direction = 0f;
				}
				
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:
				int index = event.getActionIndex();
				if (isInSpeedArea(event.getX(index))) {
					_speed = 0f;
				} else {
					_direction = 0f;
				}
				invalidate();
				break;
		}
		
		return true;
	}
	
	private boolean isInSpeedArea(float x) {
		float density = getResources().getDisplayMetrics().density;
		return x < (((OFFSITE_X * 2) + CONTROL_THIKNESS) * density);
	}
	
	private void updateSpeed(float y) {
		float center = getHeight() / 2;
		float value = center - y;
		float speed = value / _controlSize;
		
		speed = Math.min(1f, speed);
		speed = Math.max(-1f, speed);
		
		_speed = speed;
	}
	
	private void updateDirection(float x) {
		float density = getResources().getDisplayMetrics().density;
		float center = (getWidth() - (_controlSize + (OFFSITE_Y * density)));
		float value = x - center;
		float direction = value / _controlSize;
		
		direction = Math.min(1f, direction);
		direction = Math.max(-1f, direction);
		
		_direction = direction;
	}
}
