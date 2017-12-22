package com.run.treadmill.util;

import java.util.Timer;
import java.util.TimerTask;


public class BaseTimer {

	private TimerCallBack timerCallBack = null;
	private ParamRefreshTimerCallBack paramRefreshCallBack = null;
	private HrBeatTimerCallBack hrBeatTimerCallBack = null;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private boolean isContinue = false;

	public void setIsContinue(boolean isContinue) {
		this.isContinue = isContinue;
	}

	public boolean getIsContinue() {
		return isContinue;
	}

    /** 
     * 回调接口定义 
     */
    public interface TimerCallBack
    {
        public void callback();
    }

    public interface ParamRefreshTimerCallBack
    {
        public void paramRefreshProc();
    }

    public interface HrBeatTimerCallBack
    {
        public void HrBeatProc();
    }

    class LoopTask extends TimerTask {
		@Override
		public void run() {
			handleTimerOutEvent();
		}
	}

	private void handleTimerOutEvent() {
		if ( timerCallBack != null ) {
			timerCallBack.callback();
		}
		if ( paramRefreshCallBack != null ) {
			paramRefreshCallBack.paramRefreshProc();
		}
		if ( hrBeatTimerCallBack != null ) {
			hrBeatTimerCallBack.HrBeatProc();
		}
	}

	public void startTimer(long delay, long period, TimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			timerCallBack = cb;
			paramRefreshCallBack = null;
			mTimer.schedule(mTimerTask, delay, period);
		}	
	}

	public void startTimer(long delay, TimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			timerCallBack = cb;
			paramRefreshCallBack = null;
			mTimer.schedule(mTimerTask, delay);
		}	
	}

	public void startParamRefreshTimer(long delay, long period, ParamRefreshTimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			paramRefreshCallBack = cb;
			timerCallBack = null;
			mTimer.schedule(mTimerTask, delay, period);
		}	
	}

	public void startParamHrBeat(long delay, long period, HrBeatTimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			hrBeatTimerCallBack = cb;
			timerCallBack = null;
			paramRefreshCallBack = null;
			mTimer.schedule(mTimerTask, delay, period);
		}	
	}

	public void closeTimer() {
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}		
	}
}
