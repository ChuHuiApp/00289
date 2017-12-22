package com.run.treadmill.floatWindow;


import android.content.Context;
import android.graphics.PixelFormat;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import android.os.Handler;
import android.app.Instrumentation;
import android.app.Activity;

import com.run.treadmill.R;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningParamManager.RunParamTableManager;
import com.run.treadmill.runningParamManager.RunningParam;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.systemInitParam.InitParam;
import com.run.treadmill.util.BaseTimer;
import com.run.treadmill.util.BaseTimer.HrBeatTimerCallBack;
import com.run.treadmill.util.BaseTimer.TimerCallBack;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.InterfaceUtils;
import com.run.treadmill.util.MyUtils;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;

import android.os.Message;


//进入分享界面后悬浮窗口类
public class RunParamFloatWindow implements TimerCallBack, HrBeatTimerCallBack {
	
	private WindowManager mWindowManager;
	private WindowManager.LayoutParams wmParams;
	private LinearLayout mFloatWindow = null;

	private ImageView img_pulse;
	private TextView text_level;
	private TextView text_time;
	private TextView text_distance;
	private TextView text_calories;
	private TextView text_pulse;
	private TextView text_watt;
	private TextView text_speed;
	private RelativeLayout volume_layout;
	private ImageView btn_media_volume;

	private boolean plusCount = true;
	
	private Activity mContext;
	private boolean mFloatAdded = false;
	private int scn_w = -1;
	private int scn_h = -1;
	private int float_win_w = -1;
	private int float_win_h = -1;

	private boolean isMetric = true;

	private static final String TAG = "RunParamFloatWindow";

	private RunningParam mRunningParam = null;
	private BaseTimer mRunParamRefreshTimer = new BaseTimer();
	private BaseTimer mPreparePageTimer = new BaseTimer();
	private BaseTimer mHrBeatTimer = new BaseTimer();
	private InterfaceUtils mInterfaceUtils;

	private final int RUN_VALUE_GET_TIME = 1000;
	private final int MSG_REFRESH_PARAM_VALUE = 1002;
	private final int PREPARE_PAGE_TIMER_DELAY = 1000;
	private final int MEG_COUNT_DOWN = 1001;

	private final int MSG_REFRESH_HEART = 1005;
	private final int RUN_HEART_BEART = 500;

	private FloatWindowManager mFloatWindowManager;

	private  boolean isStopRefresh = false;
	
	public RunParamFloatWindow(Context context) {

		mWindowManager = (WindowManager) ((Activity)context).getApplication().
				getSystemService(Context.WINDOW_SERVICE);
		mContext = (Activity)context;
		
		mInterfaceUtils = new InterfaceUtils();

	}
	
	public void startFloat(RunningParam runningParam, FloatWindowManager floatWindowManager) {
		Log.i(TAG, "startFloat");
		/*IsInch = Settings.getUnits(mContext);*/
		isStopRefresh = false;

		mRunningParam = runningParam;
		mFloatWindowManager = floatWindowManager;

		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);

		scn_w = dm.widthPixels;
		scn_h = dm.heightPixels;
		Log.i(TAG,"__scn_w_____"+scn_w+"_scn_h__"+scn_h);
		// float_win_w = scn_w / 3;
		// float_win_h = 1080 * float_win_w / 1920;
		float_win_w = 1280;
		float_win_h = 110;
		Log.i(TAG,"__float_win_w_____"+float_win_w+"_float_win_h__"+float_win_h);


		mFloatWindow = CreateFloatWindow(R.layout.run_param_float_window, Gravity.LEFT | Gravity.TOP,
				0, 0, float_win_w, float_win_h);
		text_level = (TextView) mFloatWindow.findViewById(R.id.text_level);
		text_time = (TextView) mFloatWindow.findViewById(R.id.text_time);
		text_distance = (TextView) mFloatWindow.findViewById(R.id.text_distance);
		text_calories = (TextView) mFloatWindow.findViewById(R.id.text_calories);
		text_pulse = (TextView) mFloatWindow.findViewById(R.id.text_pulse);
		img_pulse = (ImageView) mFloatWindow.findViewById(R.id.img_pulse);
		text_watt = (TextView) mFloatWindow.findViewById(R.id.text_watt);
		text_speed = (TextView) mFloatWindow.findViewById(R.id.text_speed);
		volume_layout = (RelativeLayout) mFloatWindow.findViewById(R.id.volume_layout);
		btn_media_volume = (ImageView) mFloatWindow.findViewById(R.id.btn_media_volume);
		
		AddView(mFloatWindow);
		volume_layout.setOnClickListener(
				new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.i(TAG, "btn_media_volume onClick");
						Support.buzzerRingOnce();
						if ( !mFloatWindowManager.getVolumeWindowVis() ) {
							mFloatWindowManager.startVolumeFloatWindow();
							mFloatWindowManager.showVolumeWindow();
						} else {
							mFloatWindowManager.hideVolumeWindow();
						}
					}
			});

		isMetric = StorageParam.getIsMetric(mContext);
		initRunningParam();
		
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
		stopTimerToRefreshParam();
	}

	public void startTimerForPrepare(boolean isContinueRun) {

		mPreparePageTimer.closeTimer();
		mRunningParam.isPreparePage = true;
		mRunningParam.countDown = InitParam.CountDown;
		mPreparePageTimer.setIsContinue(isContinueRun);
		mPreparePageTimer.startTimer(0, PREPARE_PAGE_TIMER_DELAY, this);
		mFloatWindowManager.ctrlButtonDisable();
	}
	
	public void stopTimerForPrepare() {
		mPreparePageTimer.closeTimer();
		mRunningParam.isPreparePage = false;
		mFloatWindowManager.ctrlButtonEnble();
	}
	
	public void startRefreshRunningParam() {
		/*startTimerToRefreshParam();*/
		startTimerForPrepare(false);
	}

	public void stopRefreshRunningParam() {
		isStopRefresh = true;
		stopTimerForPrepare();
		stopTimerToRefreshParam();
	}

	private void startTimerToRefreshParam() {
		mRunParamRefreshTimer.closeTimer();
		mRunningParam.isRefreshPage = true;
		mRunParamRefreshTimer.startTimer(0, RUN_VALUE_GET_TIME, this);
		startTimerToHrBeat();
		
	}

	private void stopTimerToRefreshParam() {
		mRunParamRefreshTimer.closeTimer();
		mRunningParam.isRefreshPage = false;
		stopTimerToHrBeat();
	}

	@Override
	public void callback() {
		if ( mRunningParam.isRefreshPage ) {
			mMsgHandler.sendEmptyMessage(MSG_REFRESH_PARAM_VALUE); 
		}
		if ( mRunningParam.isPreparePage ) {
			mRunningParam.countDown--;
			Message message = new Message();
			message.what = MEG_COUNT_DOWN;
			message.arg1 = mRunningParam.countDown;
			message.obj = mPreparePageTimer.getIsContinue();
			mMsgHandler.sendMessage(message);
		}
	}
	//跑步参数显示
	private Handler mMsgHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case MSG_REFRESH_PARAM_VALUE:
					runParamProc();
					break;
				case MEG_COUNT_DOWN:
					int count = msg.arg1;
					Log.d(TAG,"===================11  " + count);
					if ( count >= 1 ) {
						SerialUtils.getInstance().setBuzzerContinu(50);
					} else if ( count == 0 ) {
						SerialUtils.getInstance().setBuzzerContinu(100);
					}
					if (count == -1) {
						stopTimerForPrepare();
						Log.d(TAG," (Boolean) msg.obj " +  (Boolean) msg.obj);
						setProfileStartValue();
						startTimerToRefreshParam();
					}
					break;
				case MSG_REFRESH_HEART:
					changePluse();
					break;
			}
		}
	};

	public void runParamProc() {
		/*Log.d(TAG,"runParamProc");*/
		if ( isStopRefresh ) {
			return ;
		}
		int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
		mRunningParam.alreadyRunLevel += level;

		mRunningParam.getRunStageNum();
		/*enterErrorPage();*/

		if( mRunningParam.isShowLevelIcon() ) {
			setRunProLevel(mRunningParam.runStageNum);
		}

		/*int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];*/

		//计算距离值
		float curRunDistance = mInterfaceUtils.getRunMileage(mContext, 
				MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm), (float) (1/60.0/60.0));
		mRunningParam.alreadyRunDistance += curRunDistance;
		mRunningParam.mShowRunDistance += curRunDistance;
		Log.d("runDistance"," ==0===== " + mRunningParam.alreadyRunDistance + " timeValue " + mRunningParam.alreadyRunTime);
		float showRunDistance = mRunningParam.mShowRunDistance;
		if( mRunningParam.isSetDistance ) {
			showRunDistance = UserInfoManager.getInstance().getTargetDistance() - showRunDistance;
		}

		//计算卡路里
		float curRunCalories = mInterfaceUtils.getRunCaloriesPerSec(
				UserInfoManager.getInstance().getUserInfo(), MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm), level);
		mRunningParam.alreadyRunCalories += curRunCalories;
		mRunningParam.mShowRunCalories += curRunCalories;
		float showRunCalories = mRunningParam.mShowRunCalories;
		if( mRunningParam.isSetCalories ) {
			showRunCalories = UserInfoManager.getInstance().getTargetCalorie() - showRunCalories;
		}
		String runCaloriesStr = MyUtils.getShowIntValue(showRunCalories);

		StorageParam.setRunTotalTime(mContext, 
				StorageParam.getRunTotalTime(mContext)+1l);
		StorageParam.setRunTotalDis(mContext, curRunDistance);
		/*mRunningParam.alreadyRunLevel += level;*/

		if ( mRunningParam.isRunEnd() ) {
			Support.MusicPause(mContext);
			mRunningParam.coolDownLevel = 
					mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
			mFloatWindowManager.enterCoolDown();
		} else {

			if( !mRunningParam.isCoolDownPage  ) {
				if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) != 0f ) {
					float tempTime = UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) * 60 * 1000 -
							mRunningParam.mShowRunTime;
					text_time.setText( MyUtils.getMsToSecTimeValue(tempTime) + "" );
				} else {
					text_time.setText( MyUtils.getMsToSecTimeValue(mRunningParam.mShowRunTime) + "" );
				}
			}
			
			/*Log.d(TAG,"mRunningParam.mShowRunTime : " + mRunningParam.mShowRunTime + 
					"mRunningParam.alreadyRunTime : " + mRunningParam.alreadyRunTime);*/

			if( !isMetric )
				text_speed.setText( MyUtils.getSpeedMileOneP(UserInfoManager.getInstance().rpm) + " mph" );
			else
				text_speed.setText( MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm) + " kph" );
			text_level.setText( level + "");

			text_calories.setText( runCaloriesStr + " kcal");
			text_pulse.setText(UserInfoManager.getInstance().ecgValue + "");
			text_watt.setText( mInterfaceUtils.getWattValue(UserInfoManager.getInstance().rpm, level) + "");
			if( !isMetric )
				text_distance.setText(MyUtils.getDistanceMileValue(mContext, showRunDistance) + "mile");
			else
				text_distance.setText(MyUtils.getDistanceValue(mContext, showRunDistance) + " km");
			if( mRunningParam.isAccOneMin() ) {
				Log.d(TAG,"isAccOneMin set speed incline hr list");
				UserInfoManager.getInstance().addLevelList(level, mRunningParam.alreadyRunTime);
				UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 
						mRunningParam.alreadyRunTime);
			}
		}

		
		enterErrorPage();
	}

	public void enterErrorPage() {
		if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_NO_HR_STATUS ) {
			mFloatWindowManager.enterParentPage();
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_OVERPULSE_STATUS ) {
			mFloatWindowManager.enterParentPage();
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.HRC_AUTO_END_STATUS ) {
			mFloatWindowManager.enterParentPage();
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.NO_RPM_STATUS ) {
			mFloatWindowManager.enterParentPage();
		} else if( mRunningParam.HRC_RUN_STATUS == CTConstant.FITNESS_OVER_TAG_HR ) {
			mFloatWindowManager.enterParentPage();
		}
	}

	public void initRunningParam() {
		if( UserInfoManager.getInstance().sysRunStatus == SYSRUNSTATUS.STA_MOT.ordinal() ) {
			startTimerToRefreshParam();
		} else {
			System.arraycopy( RunParamTableManager.getInstance(mContext).
					proLevelTable[UserInfoManager.getInstance().getRunMode()], 0, mRunningParam.mLevelItemValueArray,
					0, InitParam.TOTAL_RUN_INTERVAL_NUM );
			mRunningParam.isSetTime = true;
			mRunningParam.runStageNum = 0;
			if( UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) == 0f ) {
				mRunningParam.runMaxTime = InitParam.MAX_RUN_TIME;
				mRunningParam.incycleRun = true;
				

				mRunningParam.runStageNum = 0;
			}
		}
		
		text_time.setText( MyUtils.getMsToSecTimeValue(
				UserInfoManager.getInstance().getTargetTime(UserInfoManager.getInstance().getRunMode()) * 60 * 1000) + "" );
		text_level.setText( mRunningParam.mLevelItemValueArray[0] + "");
		
		if( !isMetric ) {
			text_distance.setText( 0 + " mile");
			text_speed.setText( MyUtils.getSpeedMileOneP(UserInfoManager.getInstance().rpm) + " mph" );
		} else {
			text_distance.setText( 0 + " km");
			text_speed.setText( MyUtils.getSpeedKmOneP(UserInfoManager.getInstance().rpm) + " kph" );
		}
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

	public void setLevelValue(int isUp, int mLevel) {
		if( mRunningParam.runStageNum == -1 )
			return;
		int level = mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
		if( isUp == 1 ) {
			if( (int)level < StorageParam.getMaxLevel(mContext) ) {
				level ++;
			}
		} else if( isUp == -1 ) {
			if( (int)level > InitParam.MinLevel ) {
				level --;
			}
		} else if( isUp == 0) {
			level = mLevel;
		}

		mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum] = level;

		if( StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_VIRTUAL_MODE.ordinal() || StorageParam.getRunMode(mContext) == 
						RunMode.IDX_HOME_QUICKSTART_MODE.ordinal() ||
						StorageParam.getRunMode(mContext) == 
						RunMode.IDX_HOME_GOAL_MODE.ordinal() ) {
			for( int i = mRunningParam.runStageNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				mRunningParam.mLevelItemValueArray[i] =
						mRunningParam.mLevelItemValueArray[mRunningParam.runStageNum];
			}
		}

		setRunProLevel(mRunningParam.runStageNum);
		
	}

	public synchronized void setRunProLevel(int mShowItemNum) {
//		Log.d(TAG,"setRunProgSpeedSlope mShowItemNum : =" + mShowItemNum);
		Log.d(TAG,"setRunProgSpeedSlope mShowItemNum : =" + mShowItemNum);
		if( mShowItemNum < 0 )
			mShowItemNum = 0 ;

		UserInfoManager.getInstance().curLevel = mRunningParam.mLevelItemValueArray[mShowItemNum];

		if( StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_VIRTUAL_MODE.ordinal() || StorageParam.getRunMode(mContext) == 
						RunMode.IDX_HOME_QUICKSTART_MODE.ordinal() ) {
			for( int i = mShowItemNum; i < InitParam.TOTAL_RUN_INTERVAL_NUM; i++ ) {
				mRunningParam.mLevelItemValueArray[i] =
						mRunningParam.mLevelItemValueArray[mShowItemNum];
			}
		}

		SerialUtils.getInstance().setRunLevel(UserInfoManager.getInstance().curLevel);

		text_level.setText( UserInfoManager.getInstance().curLevel + "");
	}

	public void showFloatWindow() {
		mFloatWindow.setVisibility(View.VISIBLE);
	}

	public void hideFloatWindow() {
		mFloatWindow.setVisibility(View.GONE);
	}

	public void setProfileStartValue() {
		UserInfoManager.getInstance().addLevelList(
				mRunningParam.mLevelItemValueArray[0], 0);
		UserInfoManager.getInstance().addHrList(UserInfoManager.getInstance().ecgValue, 0);
	}

	private void startTimerToHrBeat() {
		mHrBeatTimer.closeTimer();
		mHrBeatTimer.startParamHrBeat(0, RUN_HEART_BEART, this);
	}

	private void stopTimerToHrBeat() {
		mHrBeatTimer.closeTimer();
	}

	@Override
	public void HrBeatProc() {
		if( !mRunningParam.isPausePage ) {
			mMsgHandler.sendEmptyMessage(MSG_REFRESH_HEART);
		}
	}

	public void changePluse() {
    	if( UserInfoManager.getInstance().ecgValue > 0 ) {
			if( plusCount  ) {
				img_pulse.setImageResource(R.drawable.img_pulse_2);
				plusCount = false;
			} else {
				img_pulse.setImageResource(R.drawable.img_pulse_1);
				plusCount = true;
			}
		} else {
			img_pulse.setImageResource(R.drawable.img_pulse_1);
		}
    }

}
