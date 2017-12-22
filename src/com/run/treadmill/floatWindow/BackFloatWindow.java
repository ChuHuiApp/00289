package com.run.treadmill.floatWindow;


import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.MotionEvent;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Instrumentation;

import com.run.treadmill.R;

//进入分享界面后悬浮窗口类
public class BackFloatWindow {
	
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams wmParams;
	private LinearLayout mFloatWindow = null;
	
	private Activity mContext;
	private ImageView back_float = null;
	private boolean mFloatAdded = false;
	private int scn_w = -1;
	private int scn_h = -1;
	private int float_win_w = -1;
	private int float_win_h = -1;
	
	private long last_touch_time = 0;
	private int last_x = 0;
	private int last_y = 0;
	private static final String TAG = "BackFloatWindow";

	private FloatWindowManager mFloatWindowManager;
	private boolean mIsShow = true;
	
	public BackFloatWindow(Context context) {

		mWindowManager = (WindowManager) ((Activity)context).getApplication().
			getSystemService(Context.WINDOW_SERVICE);
		mContext = (Activity)context;

	}
	
	public void startFloat(FloatWindowManager floatWindowManager) {
		Log.i(TAG, "startFloat");
		mFloatWindowManager = floatWindowManager;
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);

		scn_w = dm.widthPixels;
		scn_h = dm.heightPixels;
		Log.i(TAG,"__scn_w_____"+scn_w+"_scn_h__"+scn_h);
		// float_win_w = scn_w / 3;
		// float_win_h = 1080 * float_win_w / 1920;
		float_win_w = 115;
		float_win_h = 115;
		Log.i(TAG,"__float_win_w_____"+float_win_w+"_float_win_h__"+float_win_h);


		mFloatWindow = CreateFloatWindow(R.layout.back_float_window, Gravity.LEFT | Gravity.TOP,
				scn_w-float_win_w, scn_h/2, float_win_w, float_win_h);
		back_float = (ImageView) mFloatWindow.findViewById(R.id.btn_media_dot);
		//wmParams = (WindowManager.LayoutParams)mFloatWindow.getLayoutParams();
		AddView(mFloatWindow);
		
		setFloatViewListener();
		
		back_float.setOnClickListener(
			new OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.i(TAG, "btn_min onClick");
					//RemoveView(mFloatWindow);
					
					/*if (null != mContext) {
						((HomeActivity)mContext).mMsgHandler.
							obtainMessage(CTConstant.MSG_SEND_BACK_EVENT).sendToTarget();
					}
					simulateKey(KeyEvent.KEYCODE_BACK);*/
					if( mIsShow ) {
						mFloatWindowManager.hideFloatWindow();
						mIsShow = false;
					} else {
						mFloatWindowManager.showFloatWindow();
						mIsShow = true;
						//100冗余量
						if( wmParams.y <= float_win_h || 
								wmParams.y + float_win_h + float_win_h / 2 + 100 >= scn_h) {
							wmParams.x = scn_w-float_win_w;
							wmParams.y = scn_h/2;
							mWindowManager.updateViewLayout(mFloatWindow, wmParams);
						}
						
					}
					
					
				}
		});
		
	}
	
	private LinearLayout CreateFloatWindow(int id, int gravity, int x, int y, int w, int h) {
		Log.i(TAG, "CreateFloatWindow");
		LinearLayout mWindow = (LinearLayout) GetView(id);
		WindowManager.LayoutParams Params = new WindowManager.LayoutParams();
		Params.type = LayoutParams.TYPE_PHONE;
		Params.format = PixelFormat.RGBA_8888; 
		Params.flags = LayoutParams.FLAG_NOT_FOCUSABLE | LayoutParams.FLAG_HARDWARE_ACCELERATED;      
		Params.gravity = gravity;       
		Params.x = x;
		Params.y = y;
		Params.width = w;
		Params.height = h;
		Params.windowAnimations = android.R.style.Animation_Translucent;
		setParams(Params);
		return mWindow;
	}
	
	public View GetView(int id) {
		View vv;
		LayoutInflater inflater = LayoutInflater.from(mContext.getApplication());
		vv = (LinearLayout) inflater.inflate(id, null);
		vv.measure(
			View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED), 
			View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
		);
		return vv;
	}
	
	private void setParams(WindowManager.LayoutParams param)
	{
		wmParams = param;
	}
	
	private synchronized void RemoveView(View view) {
		Log.i(TAG, "RemoveView");
		if (view == mFloatWindow) {
			if (mFloatAdded == false) {
				return;
			}
			mWindowManager.removeView(view);
			mFloatAdded = false;	
		} 
	}

	private synchronized void AddView(View view) {
		Log.i(TAG, "addView");
		if (view == mFloatWindow) {
			if (mFloatAdded == true) {
				return;
			}
			mWindowManager.addView(view, wmParams);
			mFloatAdded = true;
		}
	}
	
	public void stopFloat() {
		Log.i(TAG, " stopFloat");
		if(mFloatWindow != null && mFloatAdded) {
			RemoveView(mFloatWindow);
			mFloatWindow = null;
		}
		
	}
	
	private void setFloatViewListener() {
		back_float.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {

				if (mFloatAdded == false) return false;

				if( mIsShow ) {
					if( event.getRawY() + float_win_w + float_win_w / 2 > scn_h || 
							event.getRawY() < float_win_w + float_win_w / 2 ) {
						return false;
					}
					wmParams.x = (int) event.getRawX() - mFloatWindow.getMeasuredWidth()/2;
					wmParams.y = (int) event.getRawY() - mFloatWindow.getMeasuredHeight()/2 - 25;
				} else {
					wmParams.x = (int) event.getRawX() - mFloatWindow.getMeasuredWidth()/2;
					wmParams.y = (int) event.getRawY() - mFloatWindow.getMeasuredHeight()/2 - 25;
				}
				/*wmParams.x = (int) event.getRawX() - mFloatWindow.getMeasuredWidth()/2;
				wmParams.y = (int) event.getRawY() - mFloatWindow.getMeasuredHeight()/2 - 25;*/
				mWindowManager.updateViewLayout(mFloatWindow, wmParams);

				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					last_touch_time = event.getEventTime();
					last_x = (int)event.getRawX();
					last_y = (int)event.getRawY();
				} else if (event.getAction() == MotionEvent.ACTION_UP) {
					if(event.getEventTime() - last_touch_time > 220) {
						return true;
					}
				}
				
				
				return false;
			}
		});

	}
	
	/**
	 * 模拟发送按键事件
	 * @param KeyCode
	 */
	public static void simulateKey(final int KeyCode) {
		new Thread() {
			public void run() {
				try {
					Instrumentation inst = new Instrumentation();
					inst.sendKeyDownUpSync(KeyCode);
				} catch (Exception e) {
					Log.e("Exception when sendKeyDownUpSync", e.toString());
				}
			}
		}.start();
	}
	
	
	
	
}
