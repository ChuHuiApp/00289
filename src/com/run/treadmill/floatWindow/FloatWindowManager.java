package com.run.treadmill.floatWindow;


import java.util.Locale;

import com.run.treadmill.db.UserInfoManager;
import com.run.treadmill.runningParamManager.RunningParam;
import com.run.treadmill.serialutil.Command.SYSRUNSTATUS;
import com.run.treadmill.util.Support;

import android.content.Context;
import android.util.Log;


public class FloatWindowManager {
	public String TAG = "FloatWindowManager";

	private static FloatWindowManager ourInstance = null;
	private Context mContext;

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

	public static FloatWindowManager getInstance(Context context) {    
        if ( null == ourInstance ) {
            synchronized (FloatWindowManager.class) {
                if (null == ourInstance ) {
                    ourInstance = new FloatWindowManager(context);                    
                }
            }
        }
        return ourInstance;
    }
			
	public FloatWindowManager(Context context) {
		mContext = context;
		//增加进入QQ界面的回退悬浮框 START
		mBackFloatWindow = new BackFloatWindow(mContext);
		mRunParamFloatWindow = new RunParamFloatWindow(mContext);
		mRunCtrlFloatWindow = new RunCtrlFloatWindow(mContext);
		mOtherBackFloatWindow  = new OtherBackFloatWindow(mContext);
		mVolumeFloatWindow  = new VolumeFloatWindow(mContext);
	}

	//增加进入QQ界面的回退悬浮框 START
	private BackFloatWindow mBackFloatWindow;
	private boolean mBackFloating = false;
	
	public void startBackFloatWindow() {
		if (mBackFloatWindow != null ) {
			if (mBackFloating == false) {
		        Log.i(TAG, "startFloatWindow");
		        mBackFloatWindow.startFloat(ourInstance);
				mBackFloating = true;
			}
		}
		// startParamFloatWindow();
	}
	public void stopBackFloatWindow() {
		if (mBackFloatWindow != null) {
            if(mBackFloating == true) {
		        Log.i(TAG, "stopFloatWindow");
		        mBackFloatWindow.stopFloat();
		        mBackFloating = false;
            }
		}
		// stopParamFloatWindow();
	}

	private RunParamFloatWindow mRunParamFloatWindow;
	private boolean mParamFloating = false;
	public void startParamFloatWindow(RunningParam mRunningParam) {
		if (mRunParamFloatWindow != null ) {
			if (mParamFloating == false) {
		        Log.i(TAG, "startParamFloatWindow");
				mRunParamFloatWindow.startFloat(mRunningParam, ourInstance);
				mParamFloating = true;
			}
		}
	}
	public void stopParamFloatWindow() {
		if (mRunParamFloatWindow != null) {
            if(mParamFloating == true) {
		        Log.i(TAG, "stopParamFloatWindow");
			    mRunParamFloatWindow.stopFloat();
		        mParamFloating = false;
            }
		}
	}

	private RunCtrlFloatWindow mRunCtrlFloatWindow;
	private boolean mCtrlFloating = false;
	public void startCtrlFloatWindow(RunningParam mRunningParam) {
		if (mRunCtrlFloatWindow != null ) {
			if (mCtrlFloating == false) {
		        Log.i(TAG, "startCtrlFloatWindow");
		        mRunCtrlFloatWindow.startFloat(mRunningParam, ourInstance);
		        mCtrlFloating = true;
			}
		}
	}
	public void stopCtrlFloatWindow() {
		if (mRunCtrlFloatWindow != null) {
            if(mCtrlFloating == true) {
		        Log.i(TAG, "stopCtrlFloatWindow");
		        mRunCtrlFloatWindow.stopFloat();
		        mCtrlFloating = false;
            }
		}
	}

/*	public void quickStartMedia(Context context) {
		ourInstance.startBackFloatWindow();
		ourInstance.startParamFloatWindow( new RunningParamQuick(context) );
		ourInstance.startCtrlFloatWindow();
	}*/

	public void runningActivityStartMedia(RunningParam mRunningParam) {
		ourInstance.startBackFloatWindow();
		ourInstance.startParamFloatWindow(mRunningParam);
		ourInstance.startCtrlFloatWindow(mRunningParam);
	}

	public void stopFloatWindow() {
		stopBackFloatWindow();
		stopParamFloatWindow();
		stopCtrlFloatWindow();
		stopVolumeFloatWindow();
	}

	public void setLevelValue(int isUp, int mIncline) {
		if( mRunParamFloatWindow != null ) {
			mRunParamFloatWindow.setLevelValue(isUp, mIncline);
		}
	}

	public void startRefreshRunningParam() {
		if( mRunParamFloatWindow != null ) {
			mRunParamFloatWindow.startRefreshRunningParam();
		}
	}

	public void stopRefreshRunningParam() {
		if( mRunParamFloatWindow != null ) {
			mRunParamFloatWindow.stopRefreshRunningParam();
		}
	}

	public void ctrlButtonEnble() {
		if( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.enbleOnclickEvent();
		}
	}

	public void ctrlButtonDisable() {
		if( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.disableOnclickEvent();
		}
	}

	public void hideFloatWindow() {
		if( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.hideFloatWindow();
		}
		if( mRunParamFloatWindow != null ) {
			mRunParamFloatWindow.hideFloatWindow();
		}
		if( mVolumeFloatWindow != null && mVolumeFloatFloating) {
			mVolumeFloatWindow.hideFloatWindow();
		}
	}

	public void showFloatWindow() {
		if( mRunCtrlFloatWindow != null ) {
			mRunCtrlFloatWindow.showFloatWindow();
		}
		if( mRunParamFloatWindow != null ) {
			mRunParamFloatWindow.showFloatWindow();
		}
		/*if( mVolumeFloatWindow != null && mVolumeFloatFloating ) {
			mVolumeFloatWindow.showFloatWindow();
		}*/
	}

	public boolean quitBackHomeOrRunningSurface() {
		if( mRunCtrlFloatWindow != null ) {
			return mRunCtrlFloatWindow.quitBackHomeOrRunningSurface();
		}
		return false;
	}

	public void enterCoolDown() {
		stopRefreshRunningParam();
		stopFloatWindow();
		UserInfoManager.getInstance().mIsMediaMode = false;
		UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_COOL_DOWN.ordinal();

		Locale locale = mContext.getResources().getConfiguration().locale;

		Support.doStartApplicationWithPackageName(mContext, pkgName, 
				className[UserInfoManager.getInstance().getRunMode()]);

		Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);

	}

	public void enterParentPage() {
		stopRefreshRunningParam();
		stopFloatWindow();
		UserInfoManager.getInstance().mIsMediaMode = false;
		UserInfoManager.getInstance().sysRunStatus = SYSRUNSTATUS.STA_ERROR_PAGE.ordinal();

		Locale locale = mContext.getResources().getConfiguration().locale;

		Support.doStartApplicationWithPackageName(mContext, pkgName, 
				className[UserInfoManager.getInstance().getRunMode()]);

		Support.killThirdApp(locale.getLanguage(), mContext, UserInfoManager.getInstance().mediaMode);

	}

	//进入设置界面
	//增加进入QQ界面的回退悬浮框 START
	private OtherBackFloatWindow mOtherBackFloatWindow;
	private boolean mOtherBackFloating = false;
	
	public void startOtherBackFloatWindow() {
		if (mOtherBackFloatWindow != null ) {
			if (mOtherBackFloating == false) {
		        Log.i(TAG, "startOtherBackFloatWindow");
		        mOtherBackFloatWindow.startFloat(ourInstance);
		        mOtherBackFloating = true;
			}
		}
	}
	public void stopOtherBackFloatWindow() {
		if (mOtherBackFloatWindow != null) {
            if(mOtherBackFloating == true) {
		        Log.i(TAG, "stopOtherBackFloatWindow");
		        mOtherBackFloatWindow.stopFloat();
		        mOtherBackFloating = false;
            }
		}
	}

	//进入设置界面
	//增加volume
	private VolumeFloatWindow mVolumeFloatWindow = null;
	private boolean mVolumeFloatFloating = false;
	
	public void startVolumeFloatWindow() {
		if (mVolumeFloatWindow != null ) {
			if (mVolumeFloatFloating == false) {
		        Log.i(TAG, "startVolumeFloatWindow");
		        mVolumeFloatWindow.startFloat(ourInstance);
		        mVolumeFloatFloating = true;
			}
		}
	}
	public void stopVolumeFloatWindow() {
		if (mVolumeFloatWindow != null) {
            if(mVolumeFloatFloating == true) {
		        Log.i(TAG, "stopVolumeFloatWindow");
		        mVolumeFloatWindow.stopFloat();
		        mVolumeFloatFloating = false;
            }
		}
	}

	public void hideVolumeWindow() {
		if( mVolumeFloatWindow != null ) {
			mVolumeFloatWindow.hideFloatWindow();
		}
	}

	public void showVolumeWindow() {
		if( mVolumeFloatWindow != null ) {
			mVolumeFloatWindow.showFloatWindow();
		}
	}

	public boolean getVolumeWindowVis() {
		if ( mVolumeFloatWindow != null && mVolumeFloatFloating ) {
			return mVolumeFloatWindow.getFloatWindowIsShow();
		} else {
			return false;
		}
	}

}
