package com.hongtaili.helicopter.myView;

import com.hongtaili.helicopter.tool.MathUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.graphics.PorterDuff.Mode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.SurfaceHolder.Callback;

public class Control extends SurfaceView implements Runnable, Callback {

	private SurfaceHolder mHolder;
	private boolean isStop = false;
	private Thread mThread;
	private Paint mPaint;
	private Point mRockerPosition; // ҡ��λ��
	private Point mCtrlPoint = new Point(400, 80);// ҡ����ʼλ��
	private int mControlRadius = 20;// ҡ�˰뾶
	private int mWheelRadius = 60;// ҡ�˻��Χ�뾶
	private ControlListener listener = null; // �¼��ص��ӿ�
	private RectF powerRect = new RectF(30,0,70,150);//����������
	private float power;//������С
	private int target;//��������
	
	public static final int ACTION_RUDDER = 1, ACTION_POWER = 2; // 1��ҡ��  2���������¼�

	public Control(Context context) {
		super(context);
	}

	public Control(Context context, AttributeSet as) {
		super(context, as);
		this.setKeepScreenOn(true);
		power = powerRect.bottom;
		target = 0;
		mHolder = getHolder();
		mHolder.addCallback(this);
		mThread = new Thread(this);
		mPaint = new Paint();
		mPaint.setColor(Color.GREEN);
		mPaint.setAntiAlias(true);// �����
		mRockerPosition = new Point(mCtrlPoint);
		setFocusable(true);
		setFocusableInTouchMode(true);
		setZOrderOnTop(true);
		mHolder.setFormat(PixelFormat.TRANSPARENT);// ���ñ���͸��
	}

	// ���ûص��ӿ�
	public void setControlListener(ControlListener rockerListener) {
		listener = rockerListener;
	}

	//@Override
	public void run() {
		Canvas canvas = null;
		while (!isStop) {
			try {
				canvas = mHolder.lockCanvas();
				canvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);// �����Ļ
				mPaint.setColor(Color.CYAN);
				canvas.drawCircle(mCtrlPoint.x, mCtrlPoint.y, mWheelRadius,
						mPaint);// ���Ʒ�Χ
				mPaint.setColor(Color.RED);
				canvas.drawCircle(mRockerPosition.x, mRockerPosition.y,
						mControlRadius, mPaint);// ����ҡ��
				mPaint.setColor(Color.GRAY);
				canvas.drawRect(powerRect,mPaint);//������������Χ
				mPaint.setColor(Color.GREEN);
				canvas.drawRect(powerRect.right, powerRect.bottom,powerRect.left,power,mPaint);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (canvas != null) {
					mHolder.unlockCanvasAndPost(canvas);
				}
			}
			try {
				Thread.sleep(30);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	//@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	//@Override
	public void surfaceCreated(SurfaceHolder holder) {
		mThread.start();
	}

	//@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		isStop = true;
	}

	//@Override
	@SuppressLint({ "ParserError", "ParserError" })
	public boolean onTouchEvent(MotionEvent event) {
		int len = MathUtils.getLength(mCtrlPoint.x, mCtrlPoint.y, event.getX(),
				event.getY());
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			// �����Ļ�Ӵ��������������з�Χ��
			if (powerRect.contains(event.getX(), event.getY())) {
				power=event.getY();
				target = 1;
				if(listener != null){
					listener.onControllerChanged(ACTION_POWER, (int)(powerRect.top - power));
				}
			}
			//������ҡ������Ҳ������������
			else if(len > mWheelRadius){
				target = 0;
				return true;
			}
			//ҡ����
			else{
				target = 2;
			}
		}
		if (event.getAction() == MotionEvent.ACTION_MOVE) {
			if(target == 1){
				if(!powerRect.contains(event.getX(),event.getY())){
					//����ϵ�����������ȥ��
					power = 0;
				}
				else{
					power=event.getY();
				}
				if(listener != null){
					listener.onControllerChanged(ACTION_POWER, (int)(powerRect.top - power));
				}
			}
			else if(target == 2){
				if (len <= mWheelRadius) {
					// �����ָ��ҡ�˻��Χ�ڣ���ҡ�˴�����ָ����λ��
					mRockerPosition.set((int) event.getX(), (int) event.getY());
	
				} else {
					// ����ҡ��λ�ã�ʹ�䴦����ָ��������� ҡ�˻��Χ��Ե
					mRockerPosition = MathUtils.getBorderPoint(mCtrlPoint,
							new Point((int) event.getX(), (int) event.getY()),
							mWheelRadius);
				}
				if (listener != null) {
					float radian = MathUtils.getRadian(mCtrlPoint, new Point(
							(int) event.getX(), (int) event.getY()));
					listener.onControllerChanged(ACTION_RUDDER,
							Control.this.getAngleCouvert(radian));
				}
			}
		}
		// �����ָ�뿪��Ļ����ҡ�˷��س�ʼλ��
		if (event.getAction() == MotionEvent.ACTION_UP) {
			mRockerPosition = new Point(mCtrlPoint);
		}
		return true;
	}

	// ��ȡҡ��ƫ�ƽǶ� 0-360��
	@SuppressLint("ParserError")
	private int getAngleCouvert(float radian) {
		int tmp = (int) Math.round(radian / Math.PI * 180);
		if (tmp < 0)
			return -tmp;
		else
			return 180 + (180 - tmp);
	}
	// �ص��ӿ�
	public interface ControlListener {
		void onControllerChanged(int action, int change);
	}
}