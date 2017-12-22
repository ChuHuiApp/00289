package com.run.treadmill.floatWindow;


import java.util.Locale;

import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.app.Activity;

import com.run.treadmill.R;
import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningParamManager.RunningParam;
import com.run.treadmill.selfdefView.LongClickImage;
import com.run.treadmill.serialutil.CmdMsgHandler;
import com.run.treadmill.serialutil.Command;
import com.run.treadmill.serialutil.Command.FANSTATUS;
import com.run.treadmill.serialutil.IEventProcess;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.serialutil.SerialUtils;
import com.run.treadmill.util.CTConstant;
import com.run.treadmill.util.StorageParam;
import com.run.treadmill.util.Support;
import com.run.treadmill.util.CTConstant.RunMode;



//进入分享界面后悬浮窗口类
public class RunCtrlFloatWindow implements OnClickListener, IEventProcess {

	private WindowManager mWindowManager;
	private WindowManager.LayoutParams wmParams;
	private LinearLayout mFloatWindow = null;

	private LongClickImage btn_sportmode_down;
	private LongClickImage btn_sportmode_up;

	private ImageView btn_sportmode_start_stop;

	private ImageView btn_fan;
	private ImageView btn_back_or_home;
	
	private Activity mContext;
	private Context context;
	private boolean mFloatAdded = false;
	private int scn_w = -1;
	private int scn_h = -1;
	private int float_win_w = -1;
	private int float_win_h = -1;
	
	private final String TAG = "RunParamFloatWindow";

	private RunningParam mRunningParam = null;
	private FloatWindowManager mFloatWindowManager;
	private int START_STATUS = 0;
	private int PAUSE_STATUS = 2;
	private int runStatus = START_STATUS;

	public boolean isNeedBuzzer = true;


	
	private String pkgName = "com.run.treadmill";
	private String[] className = { 
			pkgName+".runningModeActivity.RunningHillActivity", 
			pkgName+".runningModeActivity.RunningIntervalActivity", 
			pkgName+".runningModeActivity.RunningGoalActivity",
			pkgName+".runningModeActivity.RunningQuickActivity",
			pkgName+".runningModeActivity.RunningVRActivity",
			pkgName+".runningModeActivity.RunningHRCActivity",
			pkgName+".runningModeActivity.RunningFitnessActivity",
			pkgName+".runningModeActivity.RunningUserActivity",};

	private boolean isMetric = true;
	
	public RunCtrlFloatWindow(Context context) {
		this.context = context;
		isMetric = StorageParam.getIsMetric(context);
		mWindowManager = (WindowManager) ((Activity)context).getApplication().
				getSystemService(Context.WINDOW_SERVICE);
		mContext = (Activity)context;

	}
	
	public void startFloat(RunningParam runningParam, FloatWindowManager floatWindowManager ) {
		Log.i(TAG, "startFloat");
		SerialUtils.getInstance().registerFloatWin(this);
		/*SerialUtils.getInstance().registerActivity(this);*/

		mRunningParam = runningParam;
		mFloatWindowManager = floatWindowManager;
		/*IsInch = Settings.getUnits(mContext);*/
		DisplayMetrics dm = new DisplayMetrics();
		mWindowManager.getDefaultDisplay().getMetrics(dm);

		scn_w = dm.widthPixels;
		scn_h = dm.heightPixels;
		Log.i(TAG,"__scn_w_____"+scn_w+"_scn_h__"+scn_h);
		// float_win_w = scn_w / 3;
		// float_win_h = 1080 * float_win_w / 1920;
		float_win_w = 1280;
		float_win_h = 143;
		Log.i(TAG,"__float_win_w_____"+float_win_w+"_float_win_h__"+float_win_h);


		mFloatWindow = CreateFloatWindow(R.layout.run_ctrl_float_window, Gravity.LEFT | Gravity.BOTTOM,
				0, 0, float_win_w, float_win_h);

		btn_sportmode_down = (LongClickImage) mFloatWindow.findViewById(R.id.btn_sportmode_down);
		btn_sportmode_up = (LongClickImage) mFloatWindow.findViewById(R.id.btn_sportmode_up);
		
		btn_sportmode_start_stop = (ImageView) mFloatWindow.findViewById(R.id.btn_sportmode_start_stop);
		btn_fan = (ImageView) mFloatWindow.findViewById(R.id.btn_fan);
		btn_fan.setImageResource(CTConstant.fanRes[UserInfoManager.getInstance().fanStatus-1]);
		
		btn_back_or_home = (ImageView) mFloatWindow.findViewById(R.id.btn_back_or_home);

		if( UserInfoManager.getInstance().sysRunStatus != SYSRUNSTATUS.STA_MOT.ordinal() ) {
			disableOnclickEvent();
			btn_back_or_home.setEnabled(true);
			runStatus = START_STATUS;
			btn_sportmode_start_stop.setImageResource(R.drawable.btn_sportmode_start);
			btn_sportmode_start_stop.setEnabled(true);
		} else {
			runStatus = PAUSE_STATUS;
			btn_sportmode_start_stop.setImageResource(R.drawable.btn_sportmode_stop);
			enbleOnclickEvent();
			if ( StorageParam.getRunMode(mContext) == 
					RunMode.IDX_HOME_FITNESS_MODE.ordinal() ||
					StorageParam.getRunMode(mContext) == 
							RunMode.IDX_HOME_HRC_MODE.ordinal() ) {
				btn_sportmode_up.setEnabled(false);
				btn_sportmode_down.setEnabled(false);
			}
			btn_back_or_home.setImageResource(R.drawable.btn_back);
		}
		
		AddView(mFloatWindow);

		setListenerEvent();

		btn_sportmode_down.setTag(-1);
		btn_sportmode_up.setTag(-1);

	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.btn_sportmode_down:
				Log.d(TAG,"btn_sportmode_down");
				if ( (Integer)btn_sportmode_down.getTag() != 1 ) {
					Support.buzzerRingOnce();
				} else {
					btn_sportmode_down.setTag(-1);
				}
				mFloatWindowManager.setLevelValue(-1, 0);
				break;
			case R.id.btn_sportmode_up:
				if ( (Integer)btn_sportmode_up.getTag() != 1 ) {
					Support.buzzerRingOnce();
				} else {
					btn_sportmode_up.setTag(-1);
				}
				mFloatWindowManager.setLevelValue(1, 0);
				break;
			case R.id.btn_sportmode_start_stop:
				Log.d(TAG, "START key onclick");
				if ( isNeedBuzzer == false ) {
					isNeedBuzzer = true;
				} else {
					Support.buzzerRingOnce();
				}
				startOrStopRunning();
				break;
			case R.id.btn_back_or_home:
				if ( isNeedBuzzer == false ) {
					isNeedBuzzer = true;
				} else {
					Support.buzzerRingOnce();
				}
				backHomeOrRunningSurface();
				break;
			case R.id.btn_fan:
				Support.buzzerRingOnce();
				setFanStatus();
				break;
			default:
				break;
		}
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
		SerialUtils.getInstance().unregisterFloatWin(this);
		if(mFloatWindow != null && mFloatAdded) {
			RemoveView(mFloatWindow);
			mFloatWindow = null;
		}
		
	}
	
	public void setListenerEvent() {
		btn_sportmode_down.setOnClickListener(this);
		btn_sportmode_up.setOnClickListener(this);
		
		btn_sportmode_start_stop.setOnClickListener(this);

		btn_fan.setOnClickListener(this);

		btn_back_or_home.setOnClickListener(this);
		/*btn_inclinemode_up.setLongClickRepeatListener(this, 50);*/
	}

	@SuppressWarnings("deprecation")
	public void startOrStopRunning() {

		Log.d(TAG," runStatus == " + runStatus);
		if( runStatus == START_STATUS ) {
			btn_sportmode_start_stop.setImageResource(R.drawable.btn_sportmode_stop);
			/*enbleOnclickEvent();*/
			runStatus = PAUSE_STATUS;
			btn_back_or_home.setImageResource(R.drawable.btn_back);
			mFloatWindowManager.startRefreshRunningParam();

		} else if( runStatus == PAUSE_STATUS ) {
			Locale locale = mContext.getResources().getConfiguration().locale;
//			Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);

			mFloatWindowManager.stopRefreshRunningParam();
			disableOnclickEvent();
			UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_PAUSE.ordinal();
			mFloatWindowManager.stopFloatWindow();
			Support.doStartApplicationWithPackageName(mContext, pkgName, 
					className[UserInfoManager.getInstance().getRunMode()]);
			Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);

			UserInfoManager.getInstance().mIsMediaMode = false;
			Support.MusicPause(context);

		} 
	}

	private CmdMsgHandler mCmdMsgHandler = new CmdMsgHandler(this);
	public CmdMsgHandler getCmdMsgHandler() {
		return this.mCmdMsgHandler;
	}

	@Override
	public void errorEventProc(int errorValue) {
		/*if ( mRunningParam.isPreparePage ) {
			return ;
		}*/
		if( errorValue == 1 ) {
//			releaseQuitProc();
			mCmdMsgHandler.sendMessage(
					mCmdMsgHandler.obtainMessage(Command.CMD_CURR_KEY, Command.FLOATWIN_ERROR_PROC, 0));
		}
		if( errorValue != 1 && errorValue != 0 ) {
			mCmdMsgHandler.sendMessage(
					mCmdMsgHandler.obtainMessage(Command.CMD_CURR_KEY, Command.FLOATWIN_ERROR_UNPROC, 0));
		}
	}

	public void sendMesgProc( int msg, int value) {
		if( mCmdMsgHandler != null ) {
			mCmdMsgHandler.sendMessage(
					mCmdMsgHandler.obtainMessage(Command.CMD_CURR_KEY, msg, 0));
		}		
	}
	@Override
	public void onCmdEvent(Message msg) {
		Log.d(TAG, "==========KEY_CMD_QUICK_START============");
		switch (msg.arg1) {
			case Command.FLOATWIN_ERROR_UNPROC:
				Support.keyToneOnce();
				isNeedBuzzer = false;
				btn_back_or_home.performClick();
				break;
			case Command.SAFE_KEY_TRUE:
				btn_sportmode_start_stop.setEnabled(true);
				break;
			case Command.FLOATWIN_ERROR_PROC:
				mRunningParam.isRelease = true;
				mFloatWindowManager.stopFloatWindow();
				UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_NOR.ordinal();
				Support.doStartApplicationWithPackageName(mContext, pkgName, 
						className[UserInfoManager.getInstance().getRunMode()]);
				Locale locale = mContext.getResources().getConfiguration().locale;
				Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);
				UserInfoManager.getInstance().mIsMediaMode = false;
				UserInfoManager.getInstance().mIsreleaseQuitFlag = true;
				mFloatWindowManager.stopRefreshRunningParam();
				Support.MusicPause(context);
				break;
			case Command.KEY_CMD_QUICK_START:
				Log.d(TAG, "==========KEY_CMD_QUICK_START============");
				if( runStatus == START_STATUS && btn_sportmode_start_stop.isEnabled() ) {
					Support.keyToneOnce();
					isNeedBuzzer = false;
					btn_sportmode_start_stop.performClick();
				}
				break;
			case Command.KEY_CMD_STOP_CANCEL:
				Log.d(TAG, "=============KEY_CMD_STOP_CANCEL=========");
				if( runStatus == PAUSE_STATUS && btn_sportmode_start_stop.isEnabled() ) {
					Support.keyToneOnce();
					isNeedBuzzer = false;
					btn_sportmode_start_stop.performClick();
				}
		}
		if( runStatus == START_STATUS ) {
			return ;
		}
		if( mRunningParam.isPreparePage == true ) {
			return ;
		}
		if ( StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_FITNESS_MODE.ordinal() || 
				StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_HRC_MODE.ordinal() ) {
			return ;
		}
		switch (msg.arg1) {
			case Command.KEY_CMD_LEVEL_DEC_F:
			case Command.KEY_CMD_LEVEL_DEC_S:
				Support.keyToneOnce();
				mFloatWindowManager.setLevelValue(-1, 0);
				break;
			case Command.KEY_CMD_LEVEL_PLUS_F:
			case Command.KEY_CMD_LEVEL_PLUS_S:
				Support.keyToneOnce();
				mFloatWindowManager.setLevelValue(1, 0);
				break;
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_F_LONG_2:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_1:
			case Command.KEY_CMD_LEVEL_DEC_S_LONG_2:
				mFloatWindowManager.setLevelValue(-1, 0);
				break;
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_F_LONG_2:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_1:
			case Command.KEY_CMD_LEVEL_PLUS_S_LONG_2:
				mFloatWindowManager.setLevelValue(1, 0);
				break;

			default:
				Log.d(TAG, "key value " + msg.arg1);
				break;
		}
		return ;
	}

	@SuppressWarnings("deprecation")
	public void backHomeOrRunningSurface() {
		Locale locale = mContext.getResources().getConfiguration().locale;
//		hideSoftInput();
//		Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);
		if( runStatus == START_STATUS ) {
			UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_PAUSE.ordinal();
//			MyUtils.leftAnimStartActivityForResult(mContext, new Intent(mContext, HomeActivity.class),false);
			Support.doStartApplicationWithPackageName(mContext, pkgName, 
					pkgName+".HomeActivity" );
			mFloatWindowManager.stopFloatWindow();
			Support.MusicPause(context);
		} else {
			UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_MOT.ordinal();
			mFloatWindowManager.stopFloatWindow();
			Support.doStartApplicationWithPackageName(mContext, pkgName, 
					className[UserInfoManager.getInstance().getRunMode()]);

			Support.MusicPause(context);
			Log.d(TAG,"==================== backHomeOrRunningSurface");
		}
		UserInfoManager.getInstance().mIsMediaMode = false;
		Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);
	}

	@SuppressWarnings("deprecation")
	public boolean quitBackHomeOrRunningSurface() {
		UserInfoManager.getInstance().mIsMediaMode = false;
		if( runStatus == START_STATUS ) {
			mFloatWindowManager.stopFloatWindow();
			Support.MusicPause(context);
			return true;
		} else {
			mFloatWindowManager.stopFloatWindow();
			Support.MusicPause(context);
			Log.d(TAG,"==================== backHomeOrRunningSurface");
			return false;
		}
	}

    @SuppressWarnings("deprecation")
	public void setFanStatus() {
    	if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_1_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_2_1);
    		SerialUtils.getInstance().setFanOnOff(100);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_HIGHEST_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_2_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_3_1);
    		SerialUtils.getInstance().setFanOnOff(150);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_MIDDLE_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_3_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_4_1);
    		SerialUtils.getInstance().setFanOnOff(200);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_ON_LOWEST_SPEED.ordinal();
    	} else if( btn_fan.getDrawable().getConstantState().equals(
    			mContext.getResources().getDrawable(
						R.drawable.btn_fan_4_1).getConstantState()) ) {
    		btn_fan.setImageResource(R.drawable.btn_fan_1_1);
    		SerialUtils.getInstance().setFanOnOff(0);
    		UserInfoManager.getInstance().fanStatus = FANSTATUS.TURN_OFF.ordinal();
    	}
    }

	public void enbleOnclickEvent() {
		if ( UserInfoManager.getInstance().mIsreleaseQuitFlag == true ) {
			return ;
		}
		if ( !(StorageParam.getRunMode(mContext) == 
				RunMode.IDX_HOME_FITNESS_MODE.ordinal() ||
				StorageParam.getRunMode(mContext) == 
						RunMode.IDX_HOME_HRC_MODE.ordinal()) ) {
			btn_sportmode_up.setEnabled(true);
			btn_sportmode_down.setEnabled(true);
		}

		/*btn_fan.setEnabled(true);*/
		btn_back_or_home.setEnabled(true);
		btn_sportmode_start_stop.setEnabled(true);
	}

	public void disableOnclickEvent() {
		btn_sportmode_down.setEnabled(false);
		btn_sportmode_up.setEnabled(false);
		/*btn_fan.setEnabled(false);*/
		if ( runStatus !=  START_STATUS ) {
			btn_back_or_home.setEnabled(false);
		}
		btn_sportmode_start_stop.setEnabled(false);
	}

	public void showFloatWindow() {
		mFloatWindow.setVisibility(View.VISIBLE);
	}

	public void hideFloatWindow() {
		mFloatWindow.setVisibility(View.GONE);
	}

}
