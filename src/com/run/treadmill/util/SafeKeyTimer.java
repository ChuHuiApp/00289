package com.run.treadmill.util;

import java.util.Timer;
import java.util.TimerTask;

import android.util.Log;


public class SafeKeyTimer {

	private SafeTimerCallBack timerCallBack = null;
	private Timer mTimer;
	private TimerTask mTimerTask;
	private boolean isSafe = true;

	private static SafeKeyTimer instance = null;

    /**
     * 单例模式，获取实例
     * @return
     */
	public static SafeKeyTimer getInstance() {
		synchronized (SafeKeyTimer.class) {
			if ( instance == null ) {
				synchronized (SafeKeyTimer.class) {
					instance = new SafeKeyTimer();
				}
			}
		}
		return instance;
	}

	public void setIsSafe(boolean isSafe) {
		this.isSafe = isSafe;
	}

	public boolean getIsSafe() {
		return isSafe;
	}

    /** 
     * 回调接口定义 
     */
    public interface SafeTimerCallBack
    {
        public void setSafeState();
    }

    class LoopTask extends TimerTask {
		@Override
		public void run() {
			handleTimerOutEvent();
		}
	}

	private void handleTimerOutEvent() {
		if ( timerCallBack != null ) {
			timerCallBack.setSafeState();
		}
		setIsSafe(true);
		closeTimer();
	}

	public void startTimer(long delay, long period, SafeTimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			timerCallBack = cb;
			mTimer.schedule(mTimerTask, delay, period);
			setIsSafe(false);
		}	
	}

	public void startTimer(long delay, SafeTimerCallBack cb) {
		if (mTimer == null && mTimerTask == null) {
			mTimer = new Timer(true);
			mTimerTask = new LoopTask();
			timerCallBack = cb;
			mTimer.schedule(mTimerTask, delay);
			setIsSafe(false);
			/*Log.d("startTimer"," start timer " + System.currentTimeMillis());*/
		}	
	}

	public synchronized void registerSafeCb(SafeTimerCallBack cb) {
		timerCallBack = cb;
	}	
	public synchronized void unregisterSafeCb(SafeTimerCallBack cb) {
		timerCallBack = null;
	}

	public void closeTimer() {
		isSafe = true;
		if (mTimerTask != null) {
			mTimerTask.cancel();
			mTimerTask = null;
		}
		if (mTimer != null) {
			mTimer.cancel();
			mTimer = null;
		}
		/*Log.d("startTimer"," end timer " + System.currentTimeMillis());*/
	}
}
